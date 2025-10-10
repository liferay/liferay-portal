package com.liferay.portal.spring.hibernate.exception;

import jakarta.persistence.EntityNotFoundException;

public class JpaObjectRetrievalFailureException extends ObjectRetrievalFailureException {
	public JpaObjectRetrievalFailureException(EntityNotFoundException ex) {
		super(ex.getMessage(), ex);
	}
}
