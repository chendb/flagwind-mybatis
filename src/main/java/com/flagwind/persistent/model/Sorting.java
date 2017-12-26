package com.flagwind.persistent.model;

import org.apache.commons.lang3.StringUtils;


public class Sorting {

	private SortingMode mode;
	private String[] fields;


	public Sorting() {
	}

	public Sorting(String... fields) {
		this(SortingMode.Ascending, fields);
	}

	public Sorting(SortingMode mode, String... fields) {
		if (fields == null || fields.length == 0)
			throw new IllegalArgumentException("fields");

		this.setMode(mode);
		this.fields = fields;
	}

	public String getFieldsText() {

		if (this.fields == null || this.fields.length < 1)
			return "";

		return StringUtils.join(this.fields, ",");


	}

	public void setFieldsText(String value) {

		if (StringUtils.isBlank(value))
			throw new NullPointerException("fieldText");

		this.fields = value.split(",");

	}

	public void setFields(String[] fields) {

		if (fields == null || fields.length == 0)
			throw new IllegalArgumentException("fields");

		this.fields = fields;

	}

	public String[] getFields() {

		return this.fields;

	}

	public SortingMode getMode() {
		return mode;
	}

	public void setMode(SortingMode mode) {
		this.mode = mode;
	}

	public static enum SortingMode {

		Ascending,

		Descending,
	}

	@Override
	public String toString() {

		if (StringUtils.isBlank(getFieldsText()))
			return "";

		if (SortingMode.Ascending == getMode())
			return getFieldsText() + (getMode() == null ? "" : "  ASC");
		else
			return getFieldsText() + (getMode() == null ? "" : "  DESC");
	}

}