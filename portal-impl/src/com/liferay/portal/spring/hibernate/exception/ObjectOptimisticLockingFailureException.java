package com.liferay.portal.spring.hibernate.exception;


import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.lang.Nullable;

public class ObjectOptimisticLockingFailureException extends OptimisticLockingFailureException {
	@Nullable
	private final Object persistentClass;
	@Nullable
	private final Object identifier;

	public ObjectOptimisticLockingFailureException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
		this.persistentClass = null;
		this.identifier = null;
	}

	public ObjectOptimisticLockingFailureException(Class<?> persistentClass, Object identifier) {
		this((Class)persistentClass, identifier, (Throwable)null);
	}

	public ObjectOptimisticLockingFailureException(Class<?> persistentClass, Object identifier, @Nullable Throwable cause) {
		this(persistentClass, identifier, "Object of class [" + persistentClass.getName() + "] with identifier [" + String.valueOf(identifier) + "]: optimistic locking failed", cause);
	}

	public ObjectOptimisticLockingFailureException(Class<?> persistentClass, @Nullable Object identifier, String msg, @Nullable Throwable cause) {
		super(msg, cause);
		this.persistentClass = persistentClass;
		this.identifier = identifier;
	}

	public ObjectOptimisticLockingFailureException(String persistentClassName, Object identifier) {
		this((String)persistentClassName, identifier, (Throwable)null);
	}

	public ObjectOptimisticLockingFailureException(String persistentClassName, Object identifier, @Nullable Throwable cause) {
		this(persistentClassName, identifier, "Object of class [" + persistentClassName + "] with identifier [" + String.valueOf(identifier) + "]: optimistic locking failed", cause);
	}

	public ObjectOptimisticLockingFailureException(String persistentClassName, @Nullable Object identifier, @Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
		this.persistentClass = persistentClassName;
		this.identifier = identifier;
	}

	@Nullable
	public Class<?> getPersistentClass() {
		Object var2 = this.persistentClass;
		Class var10000;
		if (var2 instanceof Class<?> clazz) {
			var10000 = clazz;
		} else {
			var10000 = null;
		}

		return var10000;
	}

	@Nullable
	public String getPersistentClassName() {
		Object var2 = this.persistentClass;
		if (var2 instanceof Class<?> clazz) {
			return clazz.getName();
		} else {
			return this.persistentClass != null ? this.persistentClass.toString() : null;
		}
	}

	@Nullable
	public Object getIdentifier() {
		return this.identifier;
	}
}

