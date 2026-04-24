package com.liferay.production.readiness;

import java.util.Collection;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author lily
 */
@ProviderType
public interface ProductionReadinessRule {

	public Collection<Result> check(long companyId);

	public String getCategory();

	public String getKey();

}
