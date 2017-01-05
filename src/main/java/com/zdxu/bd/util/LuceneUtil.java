package com.zdxu.bd.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneUtil {
	
	private Logger logger = LoggerFactory.getLogger(LuceneUtil.class);

	// 存放对应的索引文件的directory，便于读取的时候使用。
	private Map<String, Directory> LUCENE_DIRECTORY_MAP = new HashMap<String, Directory>();
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41);
	
	/**
	 * 初始化lucene，存储索引文件及具体的索引值
	 * @param datas
	 * 			待存储的索引文件列表（这里是对象列表）
	 * @param queryProperty
	 * 			作为索引的对象的域名
	 * @param type
	 * 			对象（对应索引文件）的class类型
	 */
	public void init(List<?> datas, String queryProperty, Class<?> type) {
		// RAMDirectory 将索引文件存放到内存中，提升读取速率，不适用索引文件内容过大。
		// FSDirectory 将索引文件存放到文件系统磁盘里。 FSDirectory.getDirectory(INDEX_STORE_PATH, true)
		Directory directory = new RAMDirectory();
		// 设置IndexWriter的lucene版本以及索引分析器类型
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		// 索引建立类
		IndexWriter writer = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		String className = type.getName();
		try {
			writer = new IndexWriter(directory, config);
			for(Object  obj: datas) {
				//获取列表中具体对象的指定属性的具体值
				String methodName = "get" + queryProperty.substring(0,1).toUpperCase()
						+ queryProperty.substring(1, queryProperty.length());
				Method method = obj.getClass().getMethod(methodName);
				Object value = method.invoke(obj);
				if(!(value instanceof String)) {
					logger.info("对象{}指定属性{}值无法做为索引使用，是非字符串数据或为null", className, queryProperty);
					break;
				}
				
				//将对象转换成字节数组
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos); 
				oos.writeObject(obj);
				byte[] bytes = baos.toByteArray();
				
				// lucene的文档结构，类似于数据源，存储具体的索引文件数据
				Document document = new Document();
				// 对string类型的值进行存储，既索引又分词
				document.add(new TextField("queryProperty", String.valueOf(value), Field.Store.YES));
				// 存储Field的值，可以用IndexSearcher.doc和IndexReader.document来获取此Field和存储的值
				document.add(new StoredField("classTypes", bytes));
				writer.addDocument(document);
			}
			writer.close();
			LUCENE_DIRECTORY_MAP.put(className + "_directory", directory);
		} catch (Exception e) {
			throw new RuntimeException("初始化lucene索引数据失败，失败原因：{}",e);
		} finally {
			if(baos != null) {try {baos.close();} catch (IOException e) {}}
			if(oos != null) {try {baos.close();} catch (IOException e) {}}
		}
	}
	
	/**
	 * 模糊查询，获取匹配索引的索引文件列表信息
	 * @param queryValue
	 * 			模糊查询的值
	 * @param type
	 * 			对象（索引文件）的class类型
	 * @return
	 */
	public List<?> query(String queryValue, Class<?> type) {
		List<Object> list = new ArrayList<Object>();
		Directory directory = LUCENE_DIRECTORY_MAP.get(type.getName() + "_directory");
		// 读取索引文件类
		IndexReader reader = null;
		// 索引查询
		Query query = null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// query解析器
			query = new QueryParser(Version.LUCENE_41, "queryProperty", analyzer).parse(queryValue);
			// 利用directoryreader创建指定directory的indexReader
			reader = DirectoryReader.open(directory);
			// 索引查询类
			IndexSearcher searcher = new IndexSearcher(reader);
			// 指向相匹配的搜索条件的前10个搜索结果
			TopDocs topDocs = searcher.search(query, 10);
			ScoreDoc[] sdcs = topDocs.scoreDocs;
			for (int i = 0; sdcs != null && i < sdcs.length; i++) {
				ScoreDoc sdc = sdcs[i];
				int docID = sdc.doc;
				Document document = searcher.doc(docID);
				BytesRef br = document.getBinaryValue("classBytes");
				byte[] bytes = br.bytes;
				bais = new ByteArrayInputStream(bytes); 
				ois = new ObjectInputStream(bais);
				Object obj = ois.readObject();
				list.add(obj);
			}
			reader.close();
		} catch (Exception e) {
			throw new RuntimeException("lucene索引查询失败，原因：{}", e);
		} finally {
			if(bais != null) {try {bais.close();} catch (IOException e) {}}
			if(ois != null) {try {ois.close();} catch (IOException e) {}}
		}
		return list;
	}
	
}
