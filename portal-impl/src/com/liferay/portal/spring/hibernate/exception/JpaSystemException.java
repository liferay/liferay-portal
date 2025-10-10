package com.liferay.portal.spring.hibernate.exception;

import org.springframework.dao.UncategorizedDataAccessException;

public class JpaSystemException extends UncategorizedDataAccessException {
	public JpaSystemException(RuntimeException ex) {
		super(ex.getMessage(), ex);
	}
}
