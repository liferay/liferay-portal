/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipWriter;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <p>
 * Holds context information that is used during exporting and importing portlet
 * data.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Máté Thurzó
 */
@ProviderType
public interface PortletDataContext extends Serializable {

	public static final String REFERENCE_TYPE_CHILD = "child";

	public static final String REFERENCE_TYPE_DEPENDENCY = "dependency";

	public static final String REFERENCE_TYPE_DEPENDENCY_DISPOSABLE =
		"disposable_dependency";

	public static final String REFERENCE_TYPE_EMBEDDED = "embedded";

	public static final String REFERENCE_TYPE_LAZY = "lazy";

	public static final String REFERENCE_TYPE_PARENT = "parent";

	public static final String REFERENCE_TYPE_STRONG = "strong";

	public static final String REFERENCE_TYPE_WEAK = "weak";

	public void addAssetCategories(
		String className, long classPK, long[] assetCategoryIds);

	public void addAssetTags(
		String className, long classPK, String[] assetTagNames);

	public void addClassedModel(
			Element element, String path, ClassedModel classedModel)
		throws PortalException;

	public void addClassedModel(
			Element element, String path, ClassedModel classedModel,
			Class<?> clazz)
		throws PortalException;

	public void addDateRangeCriteria(
		DynamicQuery dynamicQuery, String propertyName);

	public void addDeletionSystemEventStagedModelTypes(
		StagedModelType... stagedModelTypes);

	public void addLocks(Class<?> clazz, String key) throws PortalException;

	public void addLocks(String className, String key, Lock lock);

	public void addPermissions(Class<?> clazz, Serializable classPK)
		throws PortalException;

	public void addPermissions(String resourceName, long resourcePK)
		throws PortalException;

	public void addPermissions(
		String resourceName, long resourcePK, List<KeyValuePair> permissions);

	public void addPortalPermissions() throws PortalException;

	public void addPortletPermissions(String resourceName)
		throws PortalException;

	public boolean addPrimaryKey(Class<?> clazz, String primaryKey);

	public Element addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String referenceType, boolean missing);

	public Element addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String binPath, String referenceType,
		boolean missing);

	public Element addReferenceElement(
		ClassedModel referrerClassedModel, Element element,
		ClassedModel classedModel, String className, String binPath,
		String referenceType, boolean missing);

	public boolean addScopedPrimaryKey(Class<?> clazz, String primaryKey);

	public void addScopedPrimaryKeys(Collection<String> scopedPrimaryKeys);

	public void addZipEntry(String path, byte[] bytes);

	public void addZipEntry(String path, InputStream inputStream);

	public void addZipEntry(String path, Object object);

	public void addZipEntry(String path, String s);

	public void addZipEntry(String name, StringBuilder sb);

	public void cleanUpMissingReferences(ClassedModel classedModel);

	public void clearScopedPrimaryKeys();

	public ServiceContext createServiceContext(
		Element element, ClassedModel classedModel);

	public ServiceContext createServiceContext(StagedModel stagedModel);

	public ServiceContext createServiceContext(
		StagedModel stagedModel, Class<?> clazz);

	public ServiceContext createServiceContext(
		String path, ClassedModel classedModel);

	public Object fromXML(byte[] bytes);

	public Object fromXML(String xml);

	public long[] getAssetCategoryIds(Class<?> clazz, Serializable classPK);

	public Set<Long> getAssetLinkIds();

	public String[] getAssetTagNames(Class<?> clazz, Serializable classPK);

	public String[] getAssetTagNames(String className, Serializable classPK);

	public Map<String, String[]> getAssetTagNamesMap();

	public boolean getBooleanParameter(String namespace, String name);

	public boolean getBooleanParameter(
		String namespace, String name, boolean useDefaultValue);

	public ClassLoader getClassLoader();

	public long getCompanyGroupId();

	public long getCompanyId();

	public String getDataStrategy();

	public DateRange getDateRange();

	public Criterion getDateRangeCriteria(String propertyName);

	public Set<StagedModelType> getDeletionSystemEventStagedModelTypes();

	public Date getEndDate();

	public Element getExportDataElement(ClassedModel classedModel);

	public Element getExportDataElement(
		ClassedModel classedModel, String modelClassSimpleName);

	public Element getExportDataGroupElement(
		Class<? extends StagedModel> clazz);

	public Element getExportDataRootElement();

	public String getExportImportProcessId();

	public long getGroupId();

	public Element getImportDataElement(StagedModel stagedModel);

	public Element getImportDataElement(
		String name, String attribute, String value);

	public Element getImportDataGroupElement(
		Class<? extends StagedModel> clazz);

	public Element getImportDataRootElement();

	public Element getImportDataStagedModelElement(StagedModel stagedModel);

	public long[] getLayoutIds();

	public String getLayoutSetPrototypeUuid();

	public Map<String, Lock> getLocks();

	public ManifestSummary getManifestSummary();

	public Element getMissingReferenceElement(ClassedModel classedModel);

	public Element getMissingReferencesElement();

	public Object getNewPrimaryKey(String className, Object newPrimaryKey);

	public Map<?, ?> getNewPrimaryKeysMap(Class<?> clazz);

	public Map<?, ?> getNewPrimaryKeysMap(String className);

	public Map<String, Map<?, ?>> getNewPrimaryKeysMaps();

	public Map<String, String[]> getParameterMap();

	public Map<String, List<KeyValuePair>> getPermissions();

	public long getPlid();

	public String getPortletId();

	public Set<String> getPrimaryKeys();

	public Element getReferenceDataElement(
		Element parentElement, Class<?> clazz, long classPK);

	public Element getReferenceDataElement(
		Element parentElement, Class<?> clazz, long groupId, String uuid);

	public Element getReferenceDataElement(
		StagedModel parentStagedModel, Class<?> clazz, long classPK);

	public Element getReferenceDataElement(
		StagedModel parentStagedModel, Class<?> clazz, long groupId,
		String uuid);

	public List<Element> getReferenceDataElements(
		Element parentElement, Class<?> clazz, String referenceType);

	public List<Element> getReferenceDataElements(
		StagedModel parentStagedModel, Class<?> clazz);

	public List<Element> getReferenceDataElements(
		StagedModel parentStagedModel, Class<?> clazz, String referenceType);

	public Element getReferenceElement(
		Element parentElement, Class<?> clazz, long groupId, String uuid,
		String referenceType);

	public Element getReferenceElement(
		StagedModel parentStagedModel, String className, Serializable classPK);

	public Element getReferenceElement(String className, Serializable classPK);

	public List<Element> getReferenceElements(
		StagedModel parentStagedModel, Class<?> clazz);

	public String getRootPortletId();

	public long getScopeGroupId();

	public String getScopeLayoutUuid();

	public String getScopeType();

	public Set<String> getScopedPrimaryKeys();

	public long getSourceCompanyGroupId();

	public long getSourceCompanyId();

	public long getSourceGroupId();

	public long getSourceUserPersonalSiteGroupId();

	public Date getStartDate();

	public String getType();

	public long getUserId(String userUuid);

	public UserIdStrategy getUserIdStrategy();

	public long getUserPersonalSiteGroupId();

	public byte[] getZipEntryAsByteArray(String path);

	public InputStream getZipEntryAsInputStream(String path);

	public Object getZipEntryAsObject(Element element, String path);

	public Object getZipEntryAsObject(String path);

	public String getZipEntryAsString(String path);

	public List<String> getZipFolderEntries(String path);

	public ZipReader getZipReader();

	public ZipWriter getZipWriter();

	public boolean hasDateRange();

	public boolean hasNotUniquePerLayout(String dataKey);

	public boolean hasPrimaryKey(Class<?> clazz, String primaryKey);

	public boolean hasScopedPrimaryKey(Class<?> clazz, String primaryKey);

	public void importClassedModel(
			ClassedModel classedModel, ClassedModel newClassedModel)
		throws PortalException;

	public void importClassedModel(
			ClassedModel classedModel, ClassedModel newClassedModel,
			Class<?> clazz)
		throws PortalException;

	public void importLocks(Class<?> clazz, String key, String newKey)
		throws PortalException;

	public void importPermissions(
			Class<?> clazz, Serializable classPK, Serializable newClassPK)
		throws PortalException;

	public void importPermissions(
			String resourceName, long resourcePK, long newResourcePK)
		throws PortalException;

	public void importPortalPermissions() throws PortalException;

	public void importPortletPermissions(String resourceName)
		throws PortalException;

	public boolean isDataStrategyMirror();

	public boolean isDataStrategyMirrorWithOverwriting();

	public boolean isInitialPublication();

	public boolean isMissingReference(Element referenceElement);

	public boolean isModelCounted(String className, Serializable classPK);

	public default boolean isOriginalPrivateLayout() {
		return false;
	}

	public boolean isPathExportedInScope(String path);

	public boolean isPathNotProcessed(String path);

	public boolean isPathProcessed(String path);

	public boolean isPerformDirectBinaryImport();

	public boolean isPrivateLayout();

	public boolean isStagedModelCounted(StagedModel stagedModel);

	public boolean isValidateExistingDataHandler();

	public boolean isWithinDateRange(Date modifiedDate);

	public void putNotUniquePerLayout(String dataKey);

	public void removePrimaryKey(String path);

	public void setClassLoader(ClassLoader classLoader);

	public void setCompanyGroupId(long companyGroupId);

	public void setCompanyId(long companyId);

	public void setDataStrategy(String dataStrategy);

	public void setEndDate(Date endDate);

	public void setExportDataRootElement(Element exportDataRootElement);

	public void setExportImportProcessId(String exportImportProcessId);

	public void setGroupId(long groupId);

	public void setImportDataElementCacheEnabled(
		boolean importDataElementCacheEnabled);

	public void setImportDataRootElement(Element importDataRootElement);

	public void setLayoutIds(long[] layoutIds);

	public void setLayoutSetPrototypeUuid(String layoutSetPrototypeUuid);

	public void setManifestSummary(ManifestSummary manifestSummary);

	public void setMissingReferencesElement(Element missingReferencesElement);

	public void setNewLayouts(List<Layout> newLayouts);

	public void setOriginalPrivateLayout(boolean originalPrivateLayout);

	public void setParameterMap(Map<String, String[]> parameterMap);

	public void setPlid(long plid);

	public void setPortletId(String portletId);

	public void setPrivateLayout(boolean privateLayout);

	public void setScopeGroupId(long scopeGroupId);

	public void setScopeLayoutUuid(String scopeLayoutUuid);

	public void setScopeType(String scopeType);

	public void setSourceCompanyGroupId(long sourceCompanyGroupId);

	public void setSourceCompanyId(long sourceCompanyId);

	public void setSourceGroupId(long sourceGroupId);

	public void setSourceUserPersonalSiteGroupId(
		long sourceUserPersonalSiteGroupId);

	public void setStartDate(Date startDate);

	public void setType(String type);

	public void setUserIdStrategy(UserIdStrategy userIdStrategy);

	public void setUserPersonalSiteGroupId(long userPersonalSiteGroupId);

	public void setValidateExistingDataHandler(
		boolean validateExistingDataHandler);

	public void setZipReader(ZipReader zipReader);

	public void setZipWriter(ZipWriter zipWriter);

	public String toXML(Object object);

}