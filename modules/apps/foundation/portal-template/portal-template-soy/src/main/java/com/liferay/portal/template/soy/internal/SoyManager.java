/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.soy.internal;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.template.BaseMultiTemplateManager;
import com.liferay.portal.template.RestrictedTemplate;
import com.liferay.portal.template.TemplateContextHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Bruno Basto
 */
@Component(
	immediate = true,
	property = "language.type=" + TemplateConstants.LANG_TYPE_SOY,
	service = {SoyManager.class, TemplateManager.class}
)
public class SoyManager extends BaseMultiTemplateManager {

	@Override
	public void destroy() {
		templateContextHelper.removeAllHelperUtilities();
	}

	@Override
	public void destroy(ClassLoader classLoader) {
		templateContextHelper.removeHelperUtilities(classLoader);
	}

	public List<TemplateResource> getAllTemplateResources() {
		return _soyCapabilityBundleTrackerCustomizer.getAllTemplateResources();
	}

	@Override
	public String getName() {
		return TemplateConstants.LANG_TYPE_SOY;
	}

	@Override
	public void init() {
	}

	@Reference(unbind = "-")
	public void setSingleVMPool(SingleVMPool singleVMPool) {
		_soyTofuCacheHandler = new SoyTofuCacheHandler(
			(PortalCache<HashSet<TemplateResource>, SoyTofuCacheBag>)
				singleVMPool.getPortalCache(SoyTemplate.class.getName()));
	}

	@Override
	@Reference(service = SoyTemplateContextHelper.class, unbind = "-")
	public void setTemplateContextHelper(
		TemplateContextHelper templateContextHelper) {

		super.setTemplateContextHelper(templateContextHelper);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		int stateMask = Bundle.ACTIVE | Bundle.RESOLVED;

		_soyCapabilityBundleTrackerCustomizer =
			new SoyCapabilityBundleTrackerCustomizer(
				_soyTofuCacheHandler, _soyProviderCapabilityBundleRegister);

		_bundleTracker = new BundleTracker<>(
			bundleContext, stateMask, _soyCapabilityBundleTrackerCustomizer);

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();
	}

	@Override
	protected Template doGetTemplate(
		List<TemplateResource> templateResources,
		TemplateResource errorTemplateResource, boolean restricted,
		Map<String, Object> helperUtilities, boolean privileged) {

		Template template = new SoyTemplate(
			templateResources, errorTemplateResource, helperUtilities,
			(SoyTemplateContextHelper)templateContextHelper, privileged,
			_soyTofuCacheHandler);

		if (restricted) {
			template = new RestrictedTemplate(
				template, templateContextHelper.getRestrictedVariables());
		}

		return template;
	}

	@Reference(unbind = "-")
	protected void setSoyProviderCapabilityBundleRegister(
		SoyProviderCapabilityBundleRegister
			soyProviderCapabilityBundleRegister) {

		_soyProviderCapabilityBundleRegister =
			soyProviderCapabilityBundleRegister;
	}

	@Reference(unbind = "-")
	protected void setSoyTemplateBundleResourceParser(
		SoyTemplateBundleResourceParser soyTemplateBundleResourceParser) {
	}

	private BundleTracker<List<BundleCapability>> _bundleTracker;
	private SoyCapabilityBundleTrackerCustomizer
		_soyCapabilityBundleTrackerCustomizer;
	private SoyProviderCapabilityBundleRegister
		_soyProviderCapabilityBundleRegister;
	private SoyTofuCacheHandler _soyTofuCacheHandler;

}