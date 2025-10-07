/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.exportimport.kernel.exception.handler.ImportStagedModelExceptionHandler;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.spring.orm.LastSessionRecorderHelperUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;

import java.io.Serializable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Brian Wing Shun Chan
 * @author Máté Thurzó
 */
public class StagedModelDataHandlerUtil {

	public static void deleteStagedModel(
			PortletDataContext portletDataContext, Element deletionElement)
		throws PortalException {

		String className = deletionElement.attributeValue("class-name");

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				className);

		if (stagedModelDataHandler != null) {
			String extraData = deletionElement.attributeValue("extra-data");
			String uuid = deletionElement.attributeValue("uuid");

			stagedModelDataHandler.deleteStagedModel(
				uuid, portletDataContext.getScopeGroupId(), className,
				extraData);
		}
	}

	public static <T extends StagedModel> Element exportReferenceStagedModel(
			PortletDataContext portletDataContext, String referrerPortletId,
			T stagedModel)
		throws PortletDataException {

		Portlet referrerPortlet = PortletLocalServiceUtil.getPortletById(
			referrerPortletId);

		if (!ExportImportHelperUtil.isPublishDisplayedContent(
				portletDataContext, referrerPortlet)) {

			return portletDataContext.addReferenceElement(
				referrerPortlet, portletDataContext.getExportDataRootElement(),
				stagedModel, PortletDataContext.REFERENCE_TYPE_WEAK, true);
		}

		if (!ExportImportHelperUtil.isAlwaysIncludeReference(
				portletDataContext, stagedModel) ||
			!ExportImportHelperUtil.isReferenceWithinExportScope(
				portletDataContext, stagedModel)) {

			return portletDataContext.addReferenceElement(
				referrerPortlet, portletDataContext.getExportDataRootElement(),
				stagedModel, PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
				true);
		}

		exportStagedModel(portletDataContext, stagedModel);

		return portletDataContext.addReferenceElement(
			referrerPortlet, portletDataContext.getExportDataRootElement(),
			stagedModel, PortletDataContext.REFERENCE_TYPE_DEPENDENCY, false);
	}

	public static <T extends StagedModel, U extends StagedModel> Element
			exportReferenceStagedModel(
				PortletDataContext portletDataContext, T referrerStagedModel,
				U stagedModel, String referenceType)
		throws PortletDataException {

		Element referrerStagedModelElement =
			portletDataContext.getExportDataElement(referrerStagedModel);

		if (!ExportImportHelperUtil.isAlwaysIncludeReference(
				portletDataContext, stagedModel) ||
			!ExportImportHelperUtil.isReferenceWithinExportScope(
				portletDataContext, stagedModel)) {

			return portletDataContext.addReferenceElement(
				referrerStagedModel, referrerStagedModelElement, stagedModel,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
		}

		exportStagedModel(portletDataContext, stagedModel);

		return portletDataContext.addReferenceElement(
			referrerStagedModel, referrerStagedModelElement, stagedModel,
			referenceType, false);
	}

	public static <T extends StagedModel, U extends StagedModel> Element
			exportReferenceStagedModel(
				PortletDataContext portletDataContext, T referrerStagedModel,
				U stagedModel, String referenceType, String portletId)
		throws PortletDataException {

		Element referrerStagedModelElement =
			portletDataContext.getExportDataElement(referrerStagedModel);

		if (!ExportImportHelperUtil.isAlwaysIncludeReference(
				portletDataContext, stagedModel, portletId) ||
			!ExportImportHelperUtil.isReferenceWithinExportScope(
				portletDataContext, stagedModel)) {

			return portletDataContext.addReferenceElement(
				referrerStagedModel, referrerStagedModelElement, stagedModel,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
		}

		exportStagedModel(portletDataContext, stagedModel);

		return portletDataContext.addReferenceElement(
			referrerStagedModel, referrerStagedModelElement, stagedModel,
			referenceType, false);
	}

	public static <T extends StagedModel> void exportStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		if (!ExportImportHelperUtil.isReferenceWithinExportScope(
				portletDataContext, stagedModel)) {

			return;
		}

		StagedModelDataHandler<T> stagedModelDataHandler =
			_getStagedModelDataHandler(stagedModel);

		if (stagedModelDataHandler == null) {
			return;
		}

		stagedModelDataHandler.exportStagedModel(
			portletDataContext, stagedModel);
	}

	public static <T extends StagedModel> String getDisplayName(T stagedModel) {
		StagedModelDataHandler<T> stagedModelDataHandler =
			_getStagedModelDataHandler(stagedModel);

		if (stagedModelDataHandler == null) {
			return StringPool.BLANK;
		}

		return stagedModelDataHandler.getDisplayName(stagedModel);
	}

	public static Map<String, String> getReferenceAttributes(
		PortletDataContext portletDataContext, StagedModel stagedModel) {

		StagedModelDataHandler<StagedModel> stagedModelDataHandler =
			_getStagedModelDataHandler(stagedModel);

		if (stagedModelDataHandler == null) {
			return Collections.emptyMap();
		}

		return stagedModelDataHandler.getReferenceAttributes(
			portletDataContext, stagedModel);
	}

	/**
	 * Imports the staged model that is referenced by a portlet. To import a
	 * staged model referenced by another staged model, use {@link
	 * #importReferenceStagedModel(PortletDataContext, StagedModel, Class,
	 * long)}.
	 *
	 * @param  portletDataContext the portlet data context of the current
	 *         process
	 * @param  stagedModelClass the class of the referenced staged model to be
	 *         imported
	 * @param  classPK the primary key of the referenced staged model to be
	 *         imported
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public static void importReferenceStagedModel(
			PortletDataContext portletDataContext, Class<?> stagedModelClass,
			Serializable classPK)
		throws PortletDataException {

		importReferenceStagedModel(
			portletDataContext, stagedModelClass.getName(), classPK);
	}

	/**
	 * Imports the staged model that is referenced by a portlet. To import a
	 * staged model referenced by another staged model, use {@link
	 * #importReferenceStagedModel(PortletDataContext, StagedModel, String,
	 * Serializable)}.
	 *
	 * @param  portletDataContext the portlet data context of the current
	 *         process
	 * @param  stagedModelClassName the class name of the referenced staged
	 *         model to be imported
	 * @param  classPK the primary key of the referenced staged model to be
	 *         imported
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public static void importReferenceStagedModel(
			PortletDataContext portletDataContext, String stagedModelClassName,
			Serializable classPK)
		throws PortletDataException {

		Element referenceElement = portletDataContext.getReferenceElement(
			stagedModelClassName, classPK);

		doImportReferenceStagedModel(
			portletDataContext, referenceElement, stagedModelClassName);
	}

	/**
	 * Imports the staged model that is referenced by another staged model. To
	 * import a staged model referenced by a portlet, use {@link
	 * #importReferenceStagedModel(PortletDataContext, Class, long)}.
	 *
	 * @param  portletDataContext the portlet data context of the current
	 *         process
	 * @param  referrerStagedModel the staged model that references the staged
	 *         model to be imported
	 * @param  stagedModelClass the class of the referenced staged model to be
	 *         imported
	 * @param  classPK the primary key of the referenced staged model to be
	 *         imported
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public static <T extends StagedModel> void importReferenceStagedModel(
			PortletDataContext portletDataContext, T referrerStagedModel,
			Class<?> stagedModelClass, Serializable classPK)
		throws PortletDataException {

		importReferenceStagedModel(
			portletDataContext, referrerStagedModel, stagedModelClass.getName(),
			classPK);
	}

	/**
	 * Imports the staged model that is referenced by another staged model. To
	 * import a staged model referenced by a portlet, use {@link
	 * #importReferenceStagedModel(PortletDataContext, String, Serializable)}.
	 *
	 * @param  portletDataContext the portlet data context of the current
	 *         process
	 * @param  referrerStagedModel the staged model that references the staged
	 *         model to be imported
	 * @param  stagedModelClassName the class name of the referenced staged
	 *         model to be imported
	 * @param  classPK the primary key of the referenced staged model to be
	 *         imported
	 * @throws PortletDataException if a portlet data exception occurred
	 */
	public static <T extends StagedModel> void importReferenceStagedModel(
			PortletDataContext portletDataContext, T referrerStagedModel,
			String stagedModelClassName, Serializable classPK)
		throws PortletDataException {

		Element referenceElement = portletDataContext.getReferenceElement(
			referrerStagedModel, stagedModelClassName, classPK);

		doImportReferenceStagedModel(
			portletDataContext, referenceElement, stagedModelClassName);
	}

	public static void importReferenceStagedModels(
			PortletDataContext portletDataContext, Class<?> stagedModelClass)
		throws PortletDataException {

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		Element referencesElement = importDataRootElement.element("references");

		if (referencesElement == null) {
			return;
		}

		List<Element> referenceElements = referencesElement.elements();

		for (Element referenceElement : referenceElements) {
			String className = referenceElement.attributeValue("class-name");
			String stagedModelClassName = stagedModelClass.getName();

			if (!stagedModelClassName.equals(className)) {
				continue;
			}

			StagedModelDataHandler<?> stagedModelDataHandler =
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					stagedModelClassName);

			if (stagedModelDataHandler == null) {
				continue;
			}

			if (portletDataContext.isMissingReference(referenceElement)) {
				stagedModelDataHandler.importMissingReference(
					portletDataContext, referenceElement);

				continue;
			}

			importStagedModel(portletDataContext, referenceElement);
		}
	}

	public static <T extends StagedModel> void importReferenceStagedModels(
			PortletDataContext portletDataContext, T referrerStagedModel,
			Class<?> stagedModelClass)
		throws PortletDataException {

		List<Element> referenceElements =
			portletDataContext.getReferenceElements(
				referrerStagedModel, stagedModelClass);

		for (Element referenceElement : referenceElements) {
			Serializable classPK = GetterUtil.getString(
				referenceElement.attributeValue("class-pk"));

			importReferenceStagedModel(
				portletDataContext, referrerStagedModel, stagedModelClass,
				classPK);
		}
	}

	public static void importStagedModel(
			PortletDataContext portletDataContext, Element element)
		throws PortletDataException {

		StagedModel stagedModel = _getStagedModel(portletDataContext, element);

		importStagedModel(portletDataContext, stagedModel);
	}

	public static <T extends StagedModel> void importStagedModel(
			PortletDataContext portletDataContext, T stagedModel)
		throws PortletDataException {

		StagedModelDataHandler<T> stagedModelDataHandler =
			_getStagedModelDataHandler(stagedModel);

		if (stagedModelDataHandler == null) {
			return;
		}

		try {
			stagedModelDataHandler.importStagedModel(
				portletDataContext, stagedModel);
		}
		catch (PortletDataException portletDataException) {
			for (ImportStagedModelExceptionHandler
					importStagedModelExceptionHandler : _serviceTrackerList) {

				importStagedModelExceptionHandler.handle(
					portletDataContext, portletDataException, stagedModel);
			}

			throw portletDataException;
		}

		LastSessionRecorderHelperUtil.syncLastSessionState();
	}

	protected static void doImportReferenceStagedModel(
			PortletDataContext portletDataContext, Element referenceElement,
			String stagedModelClassName)
		throws PortletDataException {

		if (referenceElement == null) {
			return;
		}

		StagedModelDataHandler<?> stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				stagedModelClassName);

		if (stagedModelDataHandler == null) {
			return;
		}

		if (portletDataContext.isMissingReference(referenceElement)) {
			stagedModelDataHandler.importMissingReference(
				portletDataContext, referenceElement);

			return;
		}

		Attribute missingAttribute = referenceElement.attribute("missing");

		if ((missingAttribute != null) &&
			GetterUtil.getBoolean(missingAttribute.getValue())) {

			StagedModel stagedModel = _getReferenceStagedModel(
				portletDataContext, referenceElement);

			Element missingReferenceElement =
				portletDataContext.getMissingReferenceElement(stagedModel);

			if (missingReferenceElement != null) {
				String elementPath = missingReferenceElement.attributeValue(
					"element-path");

				if (Validator.isNotNull(elementPath)) {
					Element importDataRootElement =
						portletDataContext.getImportDataRootElement();

					try {
						Document document = SAXReaderUtil.read(
							portletDataContext.getZipEntryAsString(
								elementPath));

						portletDataContext.setImportDataRootElement(
							document.getRootElement());

						importStagedModel(portletDataContext, referenceElement);
					}
					catch (DocumentException documentException) {
						throw new RuntimeException(documentException);
					}
					finally {
						portletDataContext.setImportDataRootElement(
							importDataRootElement);
					}

					return;
				}
			}
		}

		try {
			importStagedModel(portletDataContext, referenceElement);

			return;
		}
		catch (PortletDataException portletDataException) {
			if (!(portletDataException.getCause() instanceof
					NullPointerException)) {

				throw portletDataException;
			}
		}

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		try {
			ZipReader zipReader = portletDataContext.getZipReader();

			List<String> entries = zipReader.getEntries();

			Iterator<String> iterator = entries.iterator();

			StagedModel stagedModel = _getStagedModel(
				portletDataContext, referenceElement);

			while (iterator.hasNext()) {
				String entry = iterator.next();

				if (entry.endsWith(".xml") &&
					_containsStagedModel(
						portletDataContext, entry, stagedModel)) {

					try {
						Document document = SAXReaderUtil.read(
							portletDataContext.getZipEntryAsString(entry));

						portletDataContext.setImportDataRootElement(
							document.getRootElement());

						String path = ExportImportPathUtil.getModelPath(
							stagedModel);

						portletDataContext.removePrimaryKey(path);

						importStagedModel(portletDataContext, referenceElement);

						return;
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}
				}
			}

			PortletDataException portletDataException =
				new PortletDataException();

			portletDataException.setStagedModel(stagedModel);
			portletDataException.setType(
				PortletDataException.MISSING_REFERENCE);

			throw portletDataException;
		}
		finally {
			portletDataContext.setImportDataRootElement(importDataRootElement);
		}
	}

	private static boolean _containsStagedModel(
		PortletDataContext portletDataContext, String path,
		StagedModel stagedModel) {

		XMLInputFactory xmlInputFactory =
			SecureXMLFactoryProviderUtil.newXMLInputFactory();

		try {
			XMLStreamReader xmlStreamReader =
				xmlInputFactory.createXMLStreamReader(
					portletDataContext.getZipEntryAsInputStream(path));

			Class<?> modelClass = stagedModel.getModelClass();

			String simpleName = modelClass.getSimpleName();

			while (xmlStreamReader.hasNext()) {
				int event = xmlStreamReader.next();

				if (event == XMLStreamConstants.START_ELEMENT) {
					QName qName = xmlStreamReader.getName();

					if (Objects.equals(qName.getLocalPart(), simpleName)) {
						return true;
					}
				}
			}
		}
		catch (XMLStreamException xmlStreamException) {
			if (_log.isDebugEnabled()) {
				_log.debug(xmlStreamException);
			}
		}

		return false;
	}

	private static StagedModel _getReferenceStagedModel(
		PortletDataContext portletDataContext, Element element) {

		long groupId = GetterUtil.getLong(element.attributeValue("group-id"));
		String className = element.attributeValue("class-name");
		Serializable classPK = GetterUtil.getString(
			element.attributeValue("class-pk"));

		String path = ExportImportPathUtil.getModelPath(
			groupId, className, classPK);

		StagedModel stagedModel =
			(StagedModel)portletDataContext.getZipEntryAsObject(element, path);

		if (stagedModel != null) {
			return stagedModel;
		}

		path = ExportImportPathUtil.getCompanyModelPath(
			portletDataContext.getSourceCompanyId(), className, classPK);

		return (StagedModel)portletDataContext.getZipEntryAsObject(
			element, path);
	}

	private static StagedModel _getStagedModel(
		PortletDataContext portletDataContext, Element element) {

		StagedModel stagedModel = null;

		String elementName = element.getName();

		if (elementName.equals("reference")) {
			stagedModel = _getReferenceStagedModel(portletDataContext, element);
		}
		else {
			String path = element.attributeValue("path");

			stagedModel = (StagedModel)portletDataContext.getZipEntryAsObject(
				element, path);
		}

		return stagedModel;
	}

	private static <T extends StagedModel> StagedModelDataHandler<T>
		_getStagedModelDataHandler(T stagedModel) {

		if (stagedModel == null) {
			_log.error(
				"Unable to get a staged model data handler for a null value " +
					"because a model was not exported properly");

			return null;
		}

		return (StagedModelDataHandler<T>)
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				ExportImportClassedModelUtil.getClassName(stagedModel));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagedModelDataHandlerUtil.class);

	private static final ServiceTrackerList<ImportStagedModelExceptionHandler>
		_serviceTrackerList = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(),
			ImportStagedModelExceptionHandler.class);

}