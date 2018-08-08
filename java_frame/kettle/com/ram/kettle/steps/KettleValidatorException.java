package com.ram.kettle.steps;

import com.ram.kettle.log.KettleException;

public class KettleValidatorException extends KettleException {

	private static final long serialVersionUID = 11213213123L;
	Validation field;

	public KettleValidatorException(String message) {
		super(message);
	}

	public KettleValidatorException(String message, String fieldName) {
		super(message);
		this.fieldName = fieldName;
	}

	public KettleValidatorException(Validation field, String message) {
		super(message);
		this.fieldName = field.getFieldName();
		this.field = field;
	}

	public KettleValidatorException(Validation field, String message,
			String fieldName) {
		super(message);
		this.fieldName = fieldName;
		this.field = field;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		if (field != null) {
			return field.getErrorDescription();
		}
		return super.getMessage();
	}
}
