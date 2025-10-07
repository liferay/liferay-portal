/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.validation.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.validation.ModelValidator;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class JournalArticleModelValidatorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), _ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));
	}

	@Test
	public void testValidateReferences() throws PortalException {
		ReflectionTestUtil.invoke(
			_journalArticleModelValidator, "validateReferences",
			new Class<?>[] {
				long.class, long.class, long.class, String.class, String.class,
				boolean.class, String.class, byte[].class, long.class,
				int.class, String.class
			},
			_group.getGroupId(), 0, _ddmStructure.getStructureId(),
			_ddmTemplate.getTemplateKey(), null, false, null, null, 0, 0,
			StringPool.BLANK);
	}

	@Test(expected = NoSuchStructureException.class)
	public void testValidateReferencesInvalidDDMStructureId()
		throws PortalException {

		ReflectionTestUtil.invoke(
			_journalArticleModelValidator, "validateReferences",
			new Class<?>[] {
				long.class, long.class, long.class, String.class, String.class,
				boolean.class, String.class, byte[].class, long.class,
				int.class, String.class
			},
			_group.getGroupId(), 0, -1, _ddmTemplate.getTemplateKey(), null,
			false, null, null, 0, 0, StringPool.BLANK);
	}

	@Test(expected = NoSuchFolderException.class)
	public void testValidateReferencesInvalidFolderId() throws PortalException {
		ReflectionTestUtil.invoke(
			_journalArticleModelValidator, "validateReferences",
			new Class<?>[] {
				long.class, long.class, long.class, String.class, String.class,
				boolean.class, String.class, byte[].class, long.class,
				int.class, String.class
			},
			_group.getGroupId(), -1, _ddmStructure.getStructureId(),
			_ddmTemplate.getTemplateKey(), null, false, null, null, 0, 0,
			StringPool.BLANK);
	}

	@Test
	public void testValidateReferencesSucceedsWhenURLIsInvalid()
		throws Exception {

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		Class<?> clazz = getClass();

		String content = StringUtil.read(
			clazz.getResourceAsStream(
				"dependencies/journal_article_content.xml"));

		content = StringUtil.replace(
			content, new String[] {"[$GROUP_ID]", "[$UUID]"},
			new String[] {
				String.valueOf(fileEntry.getGroupId()), fileEntry.getUuid()
			});

		ReflectionTestUtil.invoke(
			_journalArticleModelValidator, "validateReferences",
			new Class<?>[] {
				long.class, long.class, long.class, String.class, String.class,
				boolean.class, String.class, byte[].class, long.class,
				int.class, String.class
			},
			_group.getGroupId(), 0, _ddmStructure.getStructureId(),
			_ddmTemplate.getTemplateKey(), null, false, null, null, 0, 0,
			content);
	}

	private DDMStructure _ddmStructure;
	private DDMTemplate _ddmTemplate;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "model.class.name=com.liferay.journal.model.JournalArticle"
	)
	private ModelValidator<JournalArticle> _journalArticleModelValidator;

	@Inject
	private Portal _portal;

}