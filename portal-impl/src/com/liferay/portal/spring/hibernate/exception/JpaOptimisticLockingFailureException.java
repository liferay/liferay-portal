package com.liferay.portal.spring.hibernate.exception;

import jakarta.persistence.OptimisticLockException;

public class JpaOptimisticLockingFailureException extends ObjectOptimisticLockingFailureException {
	public JpaOptimisticLockingFailureException(OptimisticLockException ex) {
		super(ex.getMessage(), ex);
	}
}
