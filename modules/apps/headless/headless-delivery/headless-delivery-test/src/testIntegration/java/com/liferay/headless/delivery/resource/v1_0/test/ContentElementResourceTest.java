/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.headless.delivery.client.dto.v1_0.ContentElement;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.ContentElementResource;
import com.liferay.headless.delivery.client.serdes.v1_0.ContentElementSerDes;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class ContentElementResourceTest
	extends BaseContentElementResourceTestCase {

	@Override
	@Test
	public void testGetAssetLibraryContentElementsPage() throws Exception {
		super.testGetAssetLibraryContentElementsPage();

		String name = RandomTestUtil.randomString();

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), name), null,
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		testGetAssetLibraryContentElementsPage_addContentElement(
			depotEntry.getDepotEntryId(), randomContentElement());

		ContentElementResource.Builder builder =
			ContentElementResource.builder();

		contentElementResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"fields", "content.assetLibraryKey"
		).build();

		Page<ContentElement> page =
			contentElementResource.getAssetLibraryContentElementsPage(
				depotEntry.getDepotEntryId(), null, null, null,
				Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		JSONObject jsonObject = JSONUtil.put(
			"content", JSONUtil.put("assetLibraryKey", name));

		assertEquals(
			ContentElementSerDes.toDTO(jsonObject.toString()),
			page.fetchFirstItem());

		assertValid(page);
	}

	@Override
	@Test
	public void testGetAssetLibraryContentElementsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithSort(
			EntityField.Type.DOUBLE, _getUnsafeTriConsumer());
	}

	@Override
	@Test
	public void testGetSiteContentElementsPageWithFilterStringEquals()
		throws Exception {

		super.testGetSiteContentElementsPageWithFilterStringEquals();

		Long siteId = testGetSiteContentElementsPage_getSiteId();

		ContentElement contentElement = _toContentElement(
			DLAppTestUtil.addFileEntry(siteId));

		Page<ContentElement> page =
			contentElementResource.getSiteContentElementsPage(
				siteId, null, null, "(contentType eq 'Document')",
				Pagination.of(1, 2), null);

		assertEquals(
			Collections.singletonList(contentElement),
			(List<ContentElement>)page.getItems());
	}

	@Override
	@Test
	public void testGetSiteContentElementsPageWithSortDouble()
		throws Exception {

		testGetSiteContentElementsPageWithSort(
			EntityField.Type.DOUBLE, _getUnsafeTriConsumer());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title"};
	}

	@Override
	protected String getFilterString(
		EntityField entityField, String operator,
		ContentElement contentElement) {

		String entityFieldName = entityField.getName();

		if (entityFieldName.equals("priority")) {
			StringBundler sb = new StringBundler(5);

			sb.append(entityFieldName);
			sb.append(" ");
			sb.append(operator);
			sb.append(" ");
			sb.append(String.valueOf(_get(contentElement, entityFieldName)));

			return sb.toString();
		}

		return super.getFilterString(entityField, operator, contentElement);
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"contentType", "creatorId", "dateCreated", "dateModified"
		};
	}

	@Override
	protected ContentElement randomContentElement() throws Exception {
		ContentElement contentElement = super.randomContentElement();

		_put(contentElement, "priority", RandomTestUtil.randomDouble());

		return contentElement;
	}

	@Override
	protected ContentElement
			testGetAssetLibraryContentElementsPage_addContentElement(
				Long assetLibraryId, ContentElement contentElement)
		throws Exception {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.getDepotEntry(
			assetLibraryId);

		return _addContentElement(
			contentElement, (Double)_get(contentElement, "priority"),
			depotEntry.getGroupId());
	}

	@Override
	protected ContentElement testGetSiteContentElementsPage_addContentElement(
			Long siteId, ContentElement contentElement)
		throws Exception {

		return _addContentElement(
			contentElement, (Double)_get(contentElement, "priority"), siteId);
	}

	private ContentElement _addContentElement(
			ContentElement contentElement, Double priority, Long siteId)
		throws Exception {

		contentElement = _toContentElement(
			JournalTestUtil.addArticle(
				siteId, 0L, String.valueOf(contentElement.getId()),
				contentElement.getTitle(), contentElement.getTitle(),
				contentElement.getTitle(), priority));

		_put(contentElement, "priority", priority);

		return contentElement;
	}

	private Object _get(ContentElement contentElement, String fieldName) {
		Map<String, Object> fieldValueMap = _fieldValueMaps.get(contentElement);

		if (fieldValueMap == null) {
			return null;
		}

		return fieldValueMap.getOrDefault(fieldName, null);
	}

	private UnsafeTriConsumer
		<EntityField, ContentElement, ContentElement, Exception>
			_getUnsafeTriConsumer() {

		return (entityField, contentElement1, contentElement2) -> {
			_put(contentElement1, entityField.getName(), 0.1);
			_put(contentElement2, entityField.getName(), 0.5);
		};
	}

	private void _put(
		ContentElement contentElement, String fieldName, Object fieldValue) {

		_fieldValueMaps.computeIfAbsent(
			contentElement, key -> new HashMap<>()
		).put(
			fieldName, fieldValue
		);
	}

	private ContentElement _toContentElement(FileEntry fileEntry) {
		return new ContentElement() {
			{
				contentType = "Document";
				id = fileEntry.getFileEntryId();
				title = fileEntry.getTitle();
			}
		};
	}

	private ContentElement _toContentElement(JournalArticle journalArticle) {
		return new ContentElement() {
			{
				contentType = "StructuredContent";
				id = journalArticle.getId();
				title = journalArticle.getTitle();
			}
		};
	}

	private final Map<ContentElement, Map<String, Object>> _fieldValueMaps =
		new IdentityHashMap<>();

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

}