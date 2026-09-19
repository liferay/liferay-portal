/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.creole;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.wiki.engine.creole.internal.parser.ast.ASTNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.BaseListNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.BoldTextNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.CollectionNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ForcedEndOfLineNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.FormattedTextNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.HorizontalNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ImageNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ItalicTextNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ItemNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.LineNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ListNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.NoWikiSectionNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ParagraphNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.ScapedNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.UnformattedTextNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.UnorderedListItemNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.UnorderedListNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.WikiPageNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.link.LinkNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.table.TableDataNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.table.TableHeaderNode;
import com.liferay.wiki.engine.creole.internal.parser.ast.table.TableNode;
import com.liferay.wiki.engine.creole.internal.parser.parser.Creole10Lexer;
import com.liferay.wiki.engine.creole.internal.parser.parser.Creole10Parser;
import com.liferay.wiki.engine.creole.internal.util.WikiEngineCreoleComponentProvider;
import com.liferay.wiki.engine.creole.util.test.CreoleTestUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 * @author Manuel de la Peña
 */
public class AntlrCreoleParserTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_wikiEngineCreoleComponentProvider =
			CreoleTestUtil.getWikiEngineCreoleComponentProvider();
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.invoke(
			_wikiEngineCreoleComponentProvider, "deactivate", new Class<?>[0]);
	}

	@Test
	public void testParseCorrectlyBoldContentInListItems() throws Exception {
		BaseListNode unorderedListNode = parseBaseListNode("list-6.creole");

		Assert.assertEquals(1, unorderedListNode.getChildASTNodesCount());

		UnorderedListItemNode unorderedListItemNode =
			(UnorderedListItemNode)unorderedListNode.getChildASTNode(0);

		Assert.assertNotNull(unorderedListItemNode);

		FormattedTextNode formattedTextNode =
			(FormattedTextNode)unorderedListItemNode.getChildASTNode(1);

		BoldTextNode boldTextNode =
			(BoldTextNode)formattedTextNode.getChildASTNode(0);

		CollectionNode collectionNode =
			(CollectionNode)boldTextNode.getChildASTNode(0);

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("abcdefg", unformattedTextNode.getContent());
	}

	@Test
	public void testParseCorrectlyItalicContentInListItems() throws Exception {
		UnorderedListNode unorderedListNode =
			(UnorderedListNode)parseBaseListNode("list-5.creole");

		Assert.assertEquals(1, unorderedListNode.getChildASTNodesCount());

		UnorderedListItemNode unorderedListItemNode =
			(UnorderedListItemNode)unorderedListNode.getChildASTNode(0);

		Assert.assertNotNull(unorderedListItemNode);
		Assert.assertEquals(2, unorderedListItemNode.getChildASTNodesCount());

		FormattedTextNode formattedTextNode =
			(FormattedTextNode)unorderedListItemNode.getChildASTNode(1);

		ItalicTextNode italicTextNode =
			(ItalicTextNode)formattedTextNode.getChildASTNode(0);

		CollectionNode collectionNode =
			(CollectionNode)italicTextNode.getChildASTNode(0);

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("abcdefg", unformattedTextNode.getContent());
	}

	@Test
	public void testParseCorrectlyOneItemFirstLevel() throws Exception {
		executeFirstLevelItemListTests("list-1.creole", 1);
	}

	@Test
	public void testParseCorrectlyOneOrderedItemFirstLevel() throws Exception {
		executeFirstLevelItemListTests("list-7.creole", 1);
	}

	@Test
	public void testParseCorrectlyThreeItemFirstLevel() throws Exception {
		executeFirstLevelItemListTests("list-3.creole", 3);
	}

	@Test
	public void testParseCorrectlyThreeOrderedItemFirstLevel()
		throws Exception {

		executeFirstLevelItemListTests("list-9.creole", 3);
	}

	@Test
	public void testParseCorrectlyTwoItemFirstLevel() throws Exception {
		executeFirstLevelItemListTests("list-2.creole", 2);
	}

	@Test
	public void testParseCorrectlyTwoOrderedItemFirstLevel() throws Exception {
		executeFirstLevelItemListTests("list-8.creole", 2);
	}

	@Test
	public void testParseEmpyImageTag() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("image-4.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		ImageNode imageNode = (ImageNode)lineNode.getChildASTNode(0);

		Assert.assertEquals(StringPool.BLANK, imageNode.getLink());

		CollectionNode collectionNode = imageNode.getAltNode();

		Assert.assertNull(collectionNode);
	}

	@Test
	public void testParseHeadingBlocksMultiple() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("heading-10.creole");

		Assert.assertEquals(3, wikiPageNode.getChildASTNodesCount());
	}

	@Test
	public void testParseHorizontalBlock() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("horizontal-1.creole");

		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());
		Assert.assertTrue(
			wikiPageNode.getChildASTNode(0) instanceof HorizontalNode);
	}

	@Test
	public void testParseHorizontalMixedBlocks() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("horizontal-3.creole");

		Assert.assertEquals(3, wikiPageNode.getChildASTNodesCount());
		Assert.assertTrue(
			wikiPageNode.getChildASTNode(1) instanceof HorizontalNode);
	}

	@Test
	public void testParseHorizontalTwoBlocks() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("horizontal-2.creole");

		Assert.assertEquals(2, wikiPageNode.getChildASTNodesCount());
		Assert.assertTrue(
			wikiPageNode.getChildASTNode(0) instanceof HorizontalNode);
		Assert.assertTrue(
			wikiPageNode.getChildASTNode(1) instanceof HorizontalNode);
	}

	@Test
	public void testParseMultilineTextParagraph() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-2.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		List<ASTNode> astNodes = paragraphNode.getChildASTNodes();

		Assert.assertEquals(astNodes.toString(), 10, astNodes.size());

		for (int i = 0; i < astNodes.size(); i++) {
			ASTNode astNode = astNodes.get(i);

			LineNode lineNode = (LineNode)astNode;

			Assert.assertEquals(1, lineNode.getChildASTNodesCount());

			UnformattedTextNode unformattedTextNode =
				(UnformattedTextNode)lineNode.getChildASTNode(0);

			unformattedTextNode =
				(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

			Assert.assertEquals(
				"Simple P" + i, unformattedTextNode.getContent());
		}
	}

	@Test
	public void testParseMultipleImageTags() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("image-5.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(5, lineNode.getChildASTNodesCount());

		List<ASTNode> astNodes = lineNode.getChildASTNodes();

		for (int i = 0; i < astNodes.size();) {
			ImageNode imageNode = (ImageNode)astNodes.get(i);

			Assert.assertEquals("L" + ++i, imageNode.getLink());
		}
	}

	@Test
	public void testParseNoWikiBlock() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("nowikiblock-1.creole");

		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());
	}

	@Test
	public void testParseNoWikiBlockEmpty() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("nowikiblock-3.creole");

		NoWikiSectionNode noWikiSectionNode =
			(NoWikiSectionNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(StringPool.BLANK, noWikiSectionNode.getContent());
	}

	@Test
	public void testParseNoWikiBlockMultiple() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("nowikiblock-2.creole");

		Assert.assertEquals(3, wikiPageNode.getChildASTNodesCount());
	}

	@Test
	public void testParseNoWikiBlockNonempty() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("nowikiblock-4.creole");

		NoWikiSectionNode noWikiSectionNode =
			(NoWikiSectionNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(
			"This is a non \\empty\\ block", noWikiSectionNode.getContent());
	}

	@Test
	public void testParseOnlySpacesContentInImageTag() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("image-3.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		ImageNode imageNode = (ImageNode)lineNode.getChildASTNode(0);

		Assert.assertEquals("  ", imageNode.getLink());

		CollectionNode collectionNode = imageNode.getAltNode();

		Assert.assertNull(collectionNode);
	}

	@Test
	public void testParseSimpleImageTag() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("image-1.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		ImageNode imageNode = (ImageNode)lineNode.getChildASTNode(0);

		Assert.assertEquals("link", imageNode.getLink());

		CollectionNode collectionNode = imageNode.getAltNode();

		Assert.assertNotNull(collectionNode);
		Assert.assertEquals(1, collectionNode.size());

		List<ASTNode> astNodes = collectionNode.getASTNodes();

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)astNodes.get(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals(
			"alternative text", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleImageTagWithNoAlternative() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("image-2.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		ImageNode imageNode = (ImageNode)lineNode.getChildASTNode(0);

		Assert.assertEquals("link", imageNode.getLink());

		CollectionNode collectionNode = imageNode.getAltNode();

		Assert.assertNull(collectionNode);
	}

	@Test
	public void testParseSimpleLinkTag() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("link-1.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		LinkNode linkNode = (LinkNode)lineNode.getChildASTNode(0);

		Assert.assertEquals("link", linkNode.getLink());

		CollectionNode collectionNode = linkNode.getAltCollectionNode();

		Assert.assertNotNull(collectionNode);
		Assert.assertEquals(1, collectionNode.size());

		List<ASTNode> astNodes = collectionNode.getASTNodes();

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)astNodes.get(0);

		CollectionNode unformattedTextNodes =
			(CollectionNode)unformattedTextNode.getChildASTNode(0);

		unformattedTextNode = (UnformattedTextNode)unformattedTextNodes.get(0);

		Assert.assertEquals(
			"alternative text", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleLinkTagWithoutDescription() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("link-2.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		LinkNode linkNode = (LinkNode)lineNode.getChildASTNode(0);

		Assert.assertEquals("link", linkNode.getLink());
		Assert.assertNull(linkNode.getAltCollectionNode());
	}

	@Test
	public void testParseSimpleLinkTagWithoutDescription2() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("link-3.creole");

		Assert.assertNotNull(wikiPageNode);

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(5, lineNode.getChildASTNodesCount());

		List<ASTNode> astNodes = lineNode.getChildASTNodes();

		for (ASTNode astNode : astNodes) {
			if (!(astNode instanceof LinkNode)) {
				continue;
			}

			LinkNode linkNode = (LinkNode)astNode;

			Assert.assertEquals("L", linkNode.getLink());

			CollectionNode collectionNode = linkNode.getAltCollectionNode();

			Assert.assertNotNull(collectionNode);
			Assert.assertEquals(1, collectionNode.size());

			List<ASTNode> collectionNodeASTNodes = collectionNode.getASTNodes();

			UnformattedTextNode unformattedTextNode =
				(UnformattedTextNode)collectionNodeASTNodes.get(0);

			collectionNode =
				(CollectionNode)unformattedTextNode.getChildASTNode(0);

			unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

			Assert.assertEquals("A", unformattedTextNode.getContent());
		}
	}

	@Test
	public void testParseSimpleTextBoldAndItalics() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-6.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(1, paragraphNode.getChildASTNodesCount());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(2, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals("Text ", unformattedTextNode.getContent());

		BoldTextNode boldTextNode = (BoldTextNode)lineNode.getChildASTNode(1);

		Assert.assertEquals(1, boldTextNode.getChildASTNodesCount());

		ItalicTextNode italicTextNode =
			(ItalicTextNode)boldTextNode.getChildASTNode(0);

		CollectionNode collectionNode =
			(CollectionNode)italicTextNode.getChildASTNode(0);

		unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("ItalicAndBold", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleTextParagraph() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-1.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		List<ASTNode> astNodes = paragraphNode.getChildASTNodes();

		Assert.assertEquals(astNodes.toString(), 1, astNodes.size());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals(
			"Simple paragraph", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleTextWithBold() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-4.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(1, paragraphNode.getChildASTNodesCount());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(2, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals(
			"Text with some content in ", unformattedTextNode.getContent());

		BoldTextNode boldTextContent = (BoldTextNode)lineNode.getChildASTNode(
			1);

		FormattedTextNode formattedTextNode =
			(FormattedTextNode)boldTextContent.getChildASTNode(0);

		CollectionNode collectionNode =
			(CollectionNode)formattedTextNode.getChildASTNode(0);

		unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("bold", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleTextWithBoldAndItalics() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-5.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(1, paragraphNode.getChildASTNodesCount());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(4, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals(
			"Text with some content in ", unformattedTextNode.getContent());

		BoldTextNode boldTextNode = (BoldTextNode)lineNode.getChildASTNode(1);

		Assert.assertEquals(1, boldTextNode.getChildASTNodesCount());

		FormattedTextNode formattedTextNode =
			(FormattedTextNode)boldTextNode.getChildASTNode(0);

		CollectionNode collectionNode =
			(CollectionNode)formattedTextNode.getChildASTNode(0);

		unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("bold", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleTextWithForcedEndline() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-7.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(1, paragraphNode.getChildASTNodesCount());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(3, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals("Text with ", unformattedTextNode.getContent());

		CollectionNode collectionNode =
			(CollectionNode)lineNode.getChildASTNode(1);

		Assert.assertEquals(1, collectionNode.size());
		Assert.assertTrue(collectionNode.get(0) instanceof ForcedEndOfLineNode);

		collectionNode = (CollectionNode)lineNode.getChildASTNode(2);

		unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals(
			"forced line break", unformattedTextNode.getContent());
	}

	@Test
	public void testParseSimpleTextWithItalicTextInMultipleLines()
		throws Exception {

		Assert.assertNotNull(getWikiPageNode("text-8.creole"));
	}

	@Test
	public void testParseSimpleTextWithItalics() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-3.creole");

		Assert.assertNotNull(wikiPageNode);

		List<ASTNode> astNodes = wikiPageNode.getChildASTNodes();

		Assert.assertEquals(astNodes.toString(), 1, astNodes.size());

		ParagraphNode paragraphNode = (ParagraphNode)astNodes.get(0);

		astNodes = paragraphNode.getChildASTNodes();

		Assert.assertEquals(astNodes.toString(), 1, astNodes.size());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(2, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals(
			"Text with some content in ", unformattedTextNode.getContent());

		ItalicTextNode italicTextNode =
			(ItalicTextNode)lineNode.getChildASTNode(1);

		FormattedTextNode formattedTextNode =
			(FormattedTextNode)italicTextNode.getChildASTNode(0);

		CollectionNode collectionNode =
			(CollectionNode)formattedTextNode.getChildASTNode(0);

		unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("italic", unformattedTextNode.getContent());
	}

	@Test
	public void testParseTableMultipleRowsAndCOlumns() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("table-2.creole");

		Assert.assertNotNull(wikiPageNode);

		TableNode tableNode = (TableNode)wikiPageNode.getChildASTNode(0);

		Assert.assertNotNull(tableNode);
		Assert.assertEquals(4, tableNode.getChildASTNodesCount());

		CollectionNode collectionNode =
			(CollectionNode)tableNode.getChildASTNode(0);

		Assert.assertEquals(4, collectionNode.size());

		for (int i = 0; i < 4; ++i) {
			TableHeaderNode tableHeaderNode =
				(TableHeaderNode)collectionNode.get(i);

			Assert.assertNotNull(tableHeaderNode);

			UnformattedTextNode unformattedTextNode =
				(UnformattedTextNode)tableHeaderNode.getChildASTNode(0);

			Assert.assertNotNull(unformattedTextNode);
			Assert.assertEquals(1, unformattedTextNode.getChildASTNodesCount());

			unformattedTextNode =
				(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

			Assert.assertEquals(
				"H" + (i + 1), unformattedTextNode.getContent());
		}

		int count = 1;

		for (int row = 1; row < 4; ++row) {
			collectionNode = (CollectionNode)tableNode.getChildASTNode(row);

			Assert.assertEquals(4, collectionNode.size());

			for (int column = 0; column < 4; ++column) {
				TableDataNode tableDataNode = (TableDataNode)collectionNode.get(
					column);

				Assert.assertNotNull(tableDataNode);

				UnformattedTextNode unformattedTextNode =
					(UnformattedTextNode)tableDataNode.getChildASTNode(0);

				Assert.assertNotNull(unformattedTextNode);
				Assert.assertEquals(
					1, unformattedTextNode.getChildASTNodesCount());

				unformattedTextNode =
					(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

				Assert.assertEquals(
					"C" + count++, unformattedTextNode.getContent());
			}
		}
	}

	@Test
	public void testParseTableOneRowOneColumn() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("table-1.creole");

		Assert.assertNotNull(wikiPageNode);

		TableNode tableNode = (TableNode)wikiPageNode.getChildASTNode(0);

		Assert.assertNotNull(tableNode);
		Assert.assertEquals(2, tableNode.getChildASTNodesCount());

		CollectionNode collectionNode =
			(CollectionNode)tableNode.getChildASTNode(0);

		Assert.assertEquals(1, collectionNode.size());

		TableHeaderNode tableHeaderNode = (TableHeaderNode)collectionNode.get(
			0);

		Assert.assertNotNull(tableHeaderNode);

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)tableHeaderNode.getChildASTNode(0);

		Assert.assertNotNull(unformattedTextNode);
		Assert.assertEquals(1, unformattedTextNode.getChildASTNodesCount());

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals("H1", unformattedTextNode.getContent());

		List<ASTNode> astNodes = tableNode.getChildASTNodes();

		collectionNode = (CollectionNode)astNodes.get(1);

		Assert.assertEquals(1, collectionNode.size());

		TableDataNode tableDataNode = (TableDataNode)collectionNode.get(0);

		Assert.assertNotNull(tableDataNode);

		unformattedTextNode =
			(UnformattedTextNode)tableDataNode.getChildASTNode(0);

		Assert.assertNotNull(unformattedTextNode);
		Assert.assertEquals(1, unformattedTextNode.getChildASTNodesCount());

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals("C1.1", unformattedTextNode.getContent());
	}

	@Test
	public void testParseTextWithinAngleBrackets() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("text-9.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		List<ASTNode> astNodes = paragraphNode.getChildASTNodes();

		Assert.assertEquals(astNodes.toString(), 1, astNodes.size());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(1, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		unformattedTextNode =
			(UnformattedTextNode)unformattedTextNode.getChildASTNode(0);

		Assert.assertEquals("<<<Text>>>", unformattedTextNode.getContent());
	}

	@Test
	public void testSimpleEscapedCharacter() throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode("escape-1.creole");

		Assert.assertNotNull(wikiPageNode);
		Assert.assertEquals(1, wikiPageNode.getChildASTNodesCount());

		ParagraphNode paragraphNode =
			(ParagraphNode)wikiPageNode.getChildASTNode(0);

		Assert.assertEquals(2, paragraphNode.getChildASTNodesCount());

		LineNode lineNode = (LineNode)paragraphNode.getChildASTNode(0);

		Assert.assertEquals(2, lineNode.getChildASTNodesCount());

		UnformattedTextNode unformattedTextNode =
			(UnformattedTextNode)lineNode.getChildASTNode(0);

		ScapedNode scapedNode = (ScapedNode)unformattedTextNode.getChildASTNode(
			0);

		Assert.assertEquals("E", scapedNode.getContent());

		CollectionNode collectionNode =
			(CollectionNode)lineNode.getChildASTNode(1);

		unformattedTextNode = (UnformattedTextNode)collectionNode.get(0);

		Assert.assertEquals("SCAPED1", unformattedTextNode.getContent());
	}

	protected void executeFirstLevelItemListTests(String fileName, int count)
		throws Exception {

		BaseListNode baseListNode = parseBaseListNode(fileName);

		Assert.assertEquals(count, baseListNode.getChildASTNodesCount());

		for (ASTNode astNode : baseListNode.getChildASTNodes()) {
			ItemNode itemNode = (ItemNode)astNode;

			Assert.assertNotNull(itemNode);
			Assert.assertEquals(1, itemNode.getLevel());
		}
	}

	protected Creole10Parser getCreole10Parser(String fileName)
		throws IOException {

		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputStream);

		Creole10Lexer creole10Lexer = new Creole10Lexer(antlrInputStream);

		CommonTokenStream commonTokenStream = new CommonTokenStream(
			creole10Lexer);

		return new Creole10Parser(commonTokenStream);
	}

	protected WikiPageNode getWikiPageNode(String fileName) throws Exception {
		_creole10Parser = getCreole10Parser(fileName);

		_creole10Parser.wikipage();

		return _creole10Parser.getWikiPageNode();
	}

	protected BaseListNode parseBaseListNode(String fileName) throws Exception {
		WikiPageNode wikiPageNode = getWikiPageNode(fileName);

		ListNode listNode = (ListNode)wikiPageNode.getChildASTNode(0);

		Assert.assertNotNull(listNode);

		return (BaseListNode)listNode.getChildASTNode(0);
	}

	private Creole10Parser _creole10Parser;
	private WikiEngineCreoleComponentProvider
		_wikiEngineCreoleComponentProvider;

}