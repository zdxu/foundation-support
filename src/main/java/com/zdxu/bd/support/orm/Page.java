package com.zdxu.bd.support.orm;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class Page<T> implements Serializable {
	private static final long serialVersionUID = 3338822349721332235L;
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	public static int DEFAULT_PAGE_SIZE = 10;

	protected int currentPageNo = 1;

	protected int pageSize = DEFAULT_PAGE_SIZE;

	protected List<T> result = Collections.emptyList();

	protected long totalCount = -1L;

	protected boolean autoCount = true;

	protected String pageUrl = "errorPage.jsp";
	protected String formName;
	protected String orderBy;
	protected String order;
	private long start;

	public Page(int pageSize) {
		setPageSize(pageSize);
	}

	public Page(int pageSize, boolean autoCount) {
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}

	public Page() {
		this(DEFAULT_PAGE_SIZE);
	}

	public Page(long start, long totalSize, int pageSize, List<T> data) {
		this.pageSize = pageSize;
		this.start = start;
		this.totalCount = totalSize;
		this.result = data;
	}

	public long getTotalPageCount() {
		Assert.isTrue(this.pageSize > 0);
		if (this.totalCount % this.pageSize == 0L) {
			return this.totalCount / this.pageSize;
		}
		return this.totalCount / this.pageSize + 1L;
	}

	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(this.orderBy))
				&& (StringUtils.isNotBlank(this.order));
	}

	public int getFirstOfPage() {
		return (this.currentPageNo - 1) * this.pageSize + 1;
	}

	public int getLastOfPage() {
		return this.currentPageNo * this.pageSize;
	}

	public static int getDEFAULT_PAGE_SIZE() {
		return DEFAULT_PAGE_SIZE;
	}

	public static void setDEFAULT_PAGE_SIZE(int dEFAULTPAGESIZE) {
		DEFAULT_PAGE_SIZE = dEFAULTPAGESIZE;
	}

	public int getCurrentPageNo() {
		return this.currentPageNo;
	}

	public void setCurrentPageNo(int currentPageNo) {
		this.currentPageNo = currentPageNo;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getResult() {
		return this.result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public long getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isAutoCount() {
		return this.autoCount;
	}

	public void setAutoCount(boolean autoCount) {
		this.autoCount = autoCount;
	}

	public String getPageUrl() {
		return this.pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public long getStart() {
		return this.start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrder() {
		return this.order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public static String getAsc() {
		return "asc";
	}

	public static String getDesc() {
		return "desc";
	}

	public String getFormName() {
		return this.formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getJartJsonResult() {
		return "{total:" + getTotalCount() + ",rows:"
				+ JSONArray.fromObject(getResult()) + "}";
	}
}
