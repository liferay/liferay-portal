/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.NestedSetsTreeNodeModel;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class NestedSetsTreeManagerTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testCountAncestors() {
		testInsert();

		assertCountAncestors(1, _simpleNestedSetsTreeNodes[0]);
		assertCountAncestors(1, _simpleNestedSetsTreeNodes[1]);
		assertCountAncestors(1, _simpleNestedSetsTreeNodes[2]);
		assertCountAncestors(2, _simpleNestedSetsTreeNodes[3]);
		assertCountAncestors(2, _simpleNestedSetsTreeNodes[4]);
		assertCountAncestors(3, _simpleNestedSetsTreeNodes[5]);
		assertCountAncestors(2, _simpleNestedSetsTreeNodes[6]);
		assertCountAncestors(3, _simpleNestedSetsTreeNodes[7]);
		assertCountAncestors(3, _simpleNestedSetsTreeNodes[8]);
	}

	@Test
	public void testCountDescendants() {
		testInsert();

		assertCountDescendants(5, _simpleNestedSetsTreeNodes[0]);
		assertCountDescendants(3, _simpleNestedSetsTreeNodes[1]);
		assertCountDescendants(1, _simpleNestedSetsTreeNodes[2]);
		assertCountDescendants(2, _simpleNestedSetsTreeNodes[3]);
		assertCountDescendants(2, _simpleNestedSetsTreeNodes[4]);
		assertCountDescendants(1, _simpleNestedSetsTreeNodes[5]);
		assertCountDescendants(2, _simpleNestedSetsTreeNodes[6]);
		assertCountDescendants(1, _simpleNestedSetsTreeNodes[7]);
		assertCountDescendants(1, _simpleNestedSetsTreeNodes[8]);
	}

	@Test
	public void testDelete() {
		testInsert();

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[7]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4(7#8, 8), 9), 10)(11#1(12#6, 13), 14)" +
				"(15#2, 16)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[4]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#8, 7), 8)(9#1(10#6, 11), 12)(13#2, 14)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[0]);

		Assert.assertEquals(
			"(1#3(2#5, 3), 4)(5#8, 6)(7#1(8#6, 9), 10)(11#2, 12)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[8]);

		Assert.assertEquals(
			"(1#3(2#5, 3), 4)(5#1(6#6, 7), 8)(9#2, 10)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[2]);

		Assert.assertEquals(
			"(1#3(2#5, 3), 4)(5#1(6#6, 7), 8)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[5]);

		Assert.assertEquals(
			"(1#3, 2)(3#1(4#6, 5), 6)", _nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[1]);

		Assert.assertEquals(
			"(1#3, 2)(3#6, 4)", _nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[6]);

		Assert.assertEquals("(1#3, 2)", _nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.delete(_simpleNestedSetsTreeNodes[3]);

		Assert.assertEquals("", _nestedSetsTreeManager.toString());
	}

	@Test
	public void testGetAncestors() {
		testInsert();

		assertGetAncestors(_simpleNestedSetsTreeNodes[0]);
		assertGetAncestors(_simpleNestedSetsTreeNodes[1]);
		assertGetAncestors(_simpleNestedSetsTreeNodes[2]);
		assertGetAncestors(
			_simpleNestedSetsTreeNodes[3], _simpleNestedSetsTreeNodes[0]);
		assertGetAncestors(
			_simpleNestedSetsTreeNodes[4], _simpleNestedSetsTreeNodes[0]);
		assertGetAncestors(
			_simpleNestedSetsTreeNodes[5], _simpleNestedSetsTreeNodes[3],
			_simpleNestedSetsTreeNodes[0]);
		assertGetAncestors(
			_simpleNestedSetsTreeNodes[6], _simpleNestedSetsTreeNodes[1]);
		assertGetAncestors(
			_simpleNestedSetsTreeNodes[7], _simpleNestedSetsTreeNodes[6],
			_simpleNestedSetsTreeNodes[1]);
		assertGetAncestors(
			_simpleNestedSetsTreeNodes[8], _simpleNestedSetsTreeNodes[4],
			_simpleNestedSetsTreeNodes[0]);
	}

	@Test
	public void testGetDescendants() {
		testInsert();

		assertGetDescendants(
			_simpleNestedSetsTreeNodes[0], _simpleNestedSetsTreeNodes[3],
			_simpleNestedSetsTreeNodes[4], _simpleNestedSetsTreeNodes[5],
			_simpleNestedSetsTreeNodes[8]);
		assertGetDescendants(
			_simpleNestedSetsTreeNodes[1], _simpleNestedSetsTreeNodes[6],
			_simpleNestedSetsTreeNodes[7]);
		assertGetDescendants(_simpleNestedSetsTreeNodes[2]);
		assertGetDescendants(
			_simpleNestedSetsTreeNodes[3], _simpleNestedSetsTreeNodes[5]);
		assertGetDescendants(
			_simpleNestedSetsTreeNodes[4], _simpleNestedSetsTreeNodes[8]);
		assertGetDescendants(_simpleNestedSetsTreeNodes[5]);
		assertGetDescendants(
			_simpleNestedSetsTreeNodes[6], _simpleNestedSetsTreeNodes[7]);
		assertGetDescendants(_simpleNestedSetsTreeNodes[7]);
		assertGetDescendants(_simpleNestedSetsTreeNodes[8]);
	}

	@Test
	public void testInsert() {

		// (0)

		_nestedSetsTreeManager.insert(_simpleNestedSetsTreeNodes[0], null);

		Assert.assertEquals("(1#0, 2)", _nestedSetsTreeManager.toString());

		// (0, 1)

		_nestedSetsTreeManager.insert(_simpleNestedSetsTreeNodes[1], null);

		Assert.assertEquals(
			"(1#0, 2)(3#1, 4)", _nestedSetsTreeManager.toString());

		// (0, 1, 2)

		_nestedSetsTreeManager.insert(_simpleNestedSetsTreeNodes[2], null);

		Assert.assertEquals(
			"(1#0, 2)(3#1, 4)(5#2, 6)", _nestedSetsTreeManager.toString());

		// (0(3), 1, 2)

		_nestedSetsTreeManager.insert(
			_simpleNestedSetsTreeNodes[3], _simpleNestedSetsTreeNodes[0]);

		Assert.assertEquals(
			"(1#0(2#3, 3), 4)(5#1, 6)(7#2, 8)",
			_nestedSetsTreeManager.toString());

		// (0(3, 4), 1, 2)

		_nestedSetsTreeManager.insert(
			_simpleNestedSetsTreeNodes[4], _simpleNestedSetsTreeNodes[0]);

		Assert.assertEquals(
			"(1#0(2#3, 3)(4#4, 5), 6)(7#1, 8)(9#2, 10)",
			_nestedSetsTreeManager.toString());

		// (0(3(5), 4), 1, 2)

		_nestedSetsTreeManager.insert(
			_simpleNestedSetsTreeNodes[5], _simpleNestedSetsTreeNodes[3]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4, 7), 8)(9#1, 10)(11#2, 12)",
			_nestedSetsTreeManager.toString());

		// (0(3(5), 4), 1(6), 2)

		_nestedSetsTreeManager.insert(
			_simpleNestedSetsTreeNodes[6], _simpleNestedSetsTreeNodes[1]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4, 7), 8)(9#1(10#6, 11), 12)(13#2, 14)",
			_nestedSetsTreeManager.toString());

		// (0(3(5), 4), 1(6(7)), 2)

		_nestedSetsTreeManager.insert(
			_simpleNestedSetsTreeNodes[7], _simpleNestedSetsTreeNodes[6]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4, 7), 8)(9#1(10#6(11#7, 12), 13), 14)" +
				"(15#2, 16)",
			_nestedSetsTreeManager.toString());

		// (0(3(5), 4(8)), 1(6(7)), 2)

		_nestedSetsTreeManager.insert(
			_simpleNestedSetsTreeNodes[8], _simpleNestedSetsTreeNodes[4]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4(7#8, 8), 9), 10)(11#1(12#6(13#7, 14), " +
				"15), 16)(17#2, 18)",
			_nestedSetsTreeManager.toString());
	}

	@Test
	public void testMove() {
		testInsert();

		_nestedSetsTreeManager.move(_simpleNestedSetsTreeNodes[4], null, null);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4(7#8, 8), 9), 10)(11#1(12#6(13#7, 14), " +
				"15), 16)(17#2, 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[4], _simpleNestedSetsTreeNodes[0],
			_simpleNestedSetsTreeNodes[0]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#4(7#8, 8), 9), 10)(11#1(12#6(13#7, 14), " +
				"15), 16)(17#2, 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[4], _simpleNestedSetsTreeNodes[0],
			_simpleNestedSetsTreeNodes[2]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5), 6)(7#1(8#6(9#7, 10), 11), 12)(13#2(14#4(15" +
				"#8, 16), 17), 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[2], null, _simpleNestedSetsTreeNodes[0]);

		Assert.assertEquals(
			"(1#0(2#3(3#5, 4), 5)(6#2(7#4(8#8, 9), 10), 11), 12)(13#1(14#6(15" +
				"#7, 16), 17), 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[3], _simpleNestedSetsTreeNodes[0], null);

		Assert.assertEquals(
			"(1#0(2#2(3#4(4#8, 5), 6), 7), 8)(9#1(10#6(11#7, 12), 13), 14)(15" +
				"#3(16#5, 17), 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[1], null, _simpleNestedSetsTreeNodes[0]);

		Assert.assertEquals(
			"(1#0(2#2(3#4(4#8, 5), 6), 7)(8#1(9#6(10#7, 11), 12), 13), 14)(15" +
				"#3(16#5, 17), 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[3], null, _simpleNestedSetsTreeNodes[1]);

		Assert.assertEquals(
			"(1#0(2#2(3#4(4#8, 5), 6), 7)(8#1(9#6(10#7, 11), 12)(13#3(14#5, " +
				"15), 16), 17), 18)",
			_nestedSetsTreeManager.toString());

		_nestedSetsTreeManager.move(
			_simpleNestedSetsTreeNodes[2], _simpleNestedSetsTreeNodes[0],
			_simpleNestedSetsTreeNodes[3]);

		Assert.assertEquals(
			"(1#0(2#1(3#6(4#7, 5), 6)(7#3(8#5, 9)(10#2(11#4(12#8, 13), 14), " +
				"15), 16), 17), 18)",
			_nestedSetsTreeManager.toString());
	}

	protected void assertCountAncestors(
		long ancestorsCount,
		SimpleNestedSetsTreeNode simpleNestedSetsTreeNode) {

		Assert.assertEquals(
			ancestorsCount,
			_nestedSetsTreeManager.countAncestors(simpleNestedSetsTreeNode));
	}

	protected void assertCountDescendants(
		long childrenCount, SimpleNestedSetsTreeNode simpleNestedSetsTreeNode) {

		Assert.assertEquals(
			childrenCount,
			_nestedSetsTreeManager.countDescendants(simpleNestedSetsTreeNode));
	}

	protected void assertGetAncestors(
		SimpleNestedSetsTreeNode simpleNestedSetsTreeNode,
		SimpleNestedSetsTreeNode... ancestorSimpleNestedSetsTreeNodes) {

		List<SimpleNestedSetsTreeNode> simpleNestedSetsTreeNodes =
			new ArrayList<>(Arrays.asList(ancestorSimpleNestedSetsTreeNodes));

		simpleNestedSetsTreeNodes.add(simpleNestedSetsTreeNode);

		Collections.sort(simpleNestedSetsTreeNodes);

		Assert.assertEquals(
			simpleNestedSetsTreeNodes,
			_nestedSetsTreeManager.getAncestors(simpleNestedSetsTreeNode));
	}

	protected void assertGetDescendants(
		SimpleNestedSetsTreeNode simpleNestedSetsTreeNode,
		SimpleNestedSetsTreeNode... childSimpleNestedSetsTreeNodes) {

		List<SimpleNestedSetsTreeNode> simpleNestedSetsTreeNodes =
			new ArrayList<>(Arrays.asList(childSimpleNestedSetsTreeNodes));

		simpleNestedSetsTreeNodes.add(simpleNestedSetsTreeNode);

		Collections.sort(simpleNestedSetsTreeNodes);

		Assert.assertEquals(
			simpleNestedSetsTreeNodes,
			_nestedSetsTreeManager.getDescendants(simpleNestedSetsTreeNode));
	}

	private final NestedSetsTreeManager<SimpleNestedSetsTreeNode>
		_nestedSetsTreeManager = new MemoryNestedSetsTreeManager();
	private final SimpleNestedSetsTreeNode[] _simpleNestedSetsTreeNodes = {
		new SimpleNestedSetsTreeNode(0), new SimpleNestedSetsTreeNode(1),
		new SimpleNestedSetsTreeNode(2), new SimpleNestedSetsTreeNode(3),
		new SimpleNestedSetsTreeNode(4), new SimpleNestedSetsTreeNode(5),
		new SimpleNestedSetsTreeNode(6), new SimpleNestedSetsTreeNode(7),
		new SimpleNestedSetsTreeNode(8)
	};

	private static class SimpleNestedSetsTreeNode
		implements Cloneable, Comparable<SimpleNestedSetsTreeNode>,
				   NestedSetsTreeNodeModel {

		public SimpleNestedSetsTreeNode(long primaryKey) {
			_primaryKey = primaryKey;
		}

		@Override
		public SimpleNestedSetsTreeNode clone() {
			try {
				return (SimpleNestedSetsTreeNode)super.clone();
			}
			catch (CloneNotSupportedException cloneNotSupportedException) {
				throw new RuntimeException(cloneNotSupportedException);
			}
		}

		@Override
		public int compareTo(
			SimpleNestedSetsTreeNode simpleNestedSetsTreeNode) {

			long nestedSetsTreeNodeLeft =
				simpleNestedSetsTreeNode._nestedSetsTreeNodeLeft;

			if (_nestedSetsTreeNodeLeft > nestedSetsTreeNodeLeft) {
				return 1;
			}
			else if (_nestedSetsTreeNodeLeft == nestedSetsTreeNodeLeft) {
				return 0;
			}

			return -1;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof SimpleNestedSetsTreeNode)) {
				return false;
			}

			SimpleNestedSetsTreeNode simpleNestedSetsTreeNode =
				(SimpleNestedSetsTreeNode)object;

			if (_primaryKey == simpleNestedSetsTreeNode._primaryKey) {
				return true;
			}

			return false;
		}

		@Override
		public long getNestedSetsTreeNodeLeft() {
			return _nestedSetsTreeNodeLeft;
		}

		@Override
		public long getNestedSetsTreeNodeRight() {
			return _nestedSetsTreeNodeRight;
		}

		@Override
		public long getNestedSetsTreeNodeScopeId() {
			return 0;
		}

		@Override
		public long getPrimaryKey() {
			return _primaryKey;
		}

		@Override
		public int hashCode() {
			String string = toString();

			return string.hashCode();
		}

		@Override
		public void setNestedSetsTreeNodeLeft(long nestedSetsTreeNodeLeft) {
			_nestedSetsTreeNodeLeft = nestedSetsTreeNodeLeft;
		}

		@Override
		public void setNestedSetsTreeNodeRight(long nestedSetsTreeNodeRight) {
			_nestedSetsTreeNodeRight = nestedSetsTreeNodeRight;
		}

		@Override
		public String toString() {
			return StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, _nestedSetsTreeNodeLeft,
				StringPool.POUND, _primaryKey, StringPool.COMMA_AND_SPACE,
				_nestedSetsTreeNodeRight, StringPool.CLOSE_PARENTHESIS);
		}

		private long _nestedSetsTreeNodeLeft;
		private long _nestedSetsTreeNodeRight;
		private long _primaryKey;

	}

	private class MemoryNestedSetsTreeManager
		extends NestedSetsTreeManager<SimpleNestedSetsTreeNode> {

		@Override
		public void delete(SimpleNestedSetsTreeNode simpleNestedSetsTreeNode) {
			super.delete(simpleNestedSetsTreeNode);

			_simpleNestedSetsTreeNodeList.remove(simpleNestedSetsTreeNode);

			removeSimpleNestedSetsTreeNode(simpleNestedSetsTreeNode);

			synchronizeSimpleNestedSetsTreeNodes();
		}

		@Override
		public void insert(
			SimpleNestedSetsTreeNode simpleNestedSetsTreeNode,
			SimpleNestedSetsTreeNode parentSimpleNestedSetsTreeNode) {

			super.insert(
				simpleNestedSetsTreeNode, parentSimpleNestedSetsTreeNode);

			_simpleNestedSetsTreeNodeList.add(simpleNestedSetsTreeNode.clone());

			synchronizeSimpleNestedSetsTreeNodes();
		}

		@Override
		public String toString() {
			StringBundler sb = new StringBundler(
				_simpleNestedSetsTreeNodeList.size() * 7);

			Collections.sort(_simpleNestedSetsTreeNodeList);

			Deque<SimpleNestedSetsTreeNode> deque = new LinkedList<>();

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				long nestedSetsTreeNodeLeft =
					simpleNestedSetsTreeNode.getNestedSetsTreeNodeLeft();
				long nestedSetsTreeNodeRight =
					simpleNestedSetsTreeNode.getNestedSetsTreeNodeRight();

				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(nestedSetsTreeNodeLeft);
				sb.append(StringPool.POUND);
				sb.append(simpleNestedSetsTreeNode.getPrimaryKey());

				if ((nestedSetsTreeNodeLeft + 1) != nestedSetsTreeNodeRight) {
					deque.push(simpleNestedSetsTreeNode);

					continue;
				}

				sb.append(StringPool.COMMA_AND_SPACE);
				sb.append(nestedSetsTreeNodeRight);
				sb.append(StringPool.CLOSE_PARENTHESIS);

				SimpleNestedSetsTreeNode previousSimpleNestedSetsTreeNode =
					null;

				while (((previousSimpleNestedSetsTreeNode = deque.peek()) !=
							null) &&
					   ((nestedSetsTreeNodeRight + 1) ==
						   previousSimpleNestedSetsTreeNode.
							   getNestedSetsTreeNodeRight())) {

					sb.append(StringPool.COMMA_AND_SPACE);
					sb.append(
						previousSimpleNestedSetsTreeNode.
							getNestedSetsTreeNodeRight());
					sb.append(StringPool.CLOSE_PARENTHESIS);

					nestedSetsTreeNodeRight =
						previousSimpleNestedSetsTreeNode.
							getNestedSetsTreeNodeRight();

					deque.pop();
				}
			}

			return sb.toString();
		}

		@Override
		protected long doCountAncestors(
			long nestedSetsTreeNodeScopeId, long nestedSetsTreeNodeLeft,
			long nestedSetsTreeNodeRight) {

			long count = 0;

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				if ((nestedSetsTreeNodeLeft >=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeLeft) &&
					(nestedSetsTreeNodeRight <=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeRight)) {

					count++;
				}
			}

			return count;
		}

		@Override
		protected long doCountDescendants(
			long nestedSetsTreeNodeScopeId, long nestedSetsTreeNodeLeft,
			long nestedSetsTreeNodeRight) {

			long count = 0;

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				if ((nestedSetsTreeNodeLeft <=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeLeft) &&
					(nestedSetsTreeNodeRight >=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeRight)) {

					count++;
				}
			}

			return count;
		}

		@Override
		protected List<SimpleNestedSetsTreeNode> doGetAncestors(
			long nestedSetsTreeNodeScopeId, long nestedSetsTreeNodeLeft,
			long nestedSetsTreeNodeRight) {

			List<SimpleNestedSetsTreeNode> simpleNestedSetsTreeNodes =
				new ArrayList<>();

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				if ((nestedSetsTreeNodeLeft >=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeLeft) &&
					(nestedSetsTreeNodeRight <=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeRight)) {

					simpleNestedSetsTreeNodes.add(simpleNestedSetsTreeNode);
				}
			}

			Collections.sort(simpleNestedSetsTreeNodes);

			return simpleNestedSetsTreeNodes;
		}

		@Override
		protected List<SimpleNestedSetsTreeNode> doGetDescendants(
			long nestedSetsTreeNodeScopeId, long nestedSetsTreeNodeLeft,
			long nestedSetsTreeNodeRight) {

			List<SimpleNestedSetsTreeNode> simpleNestedSetsTreeNodes =
				new ArrayList<>();

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				if ((nestedSetsTreeNodeLeft <=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeLeft) &&
					(nestedSetsTreeNodeRight >=
						simpleNestedSetsTreeNode._nestedSetsTreeNodeRight)) {

					simpleNestedSetsTreeNodes.add(simpleNestedSetsTreeNode);
				}
			}

			Collections.sort(simpleNestedSetsTreeNodes);

			return simpleNestedSetsTreeNodes;
		}

		@Override
		protected void doUpdate(
			long nestedSetsTreeNodeScopeId, boolean leftOrRight, long delta,
			long limit, boolean inclusive) {

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				if (leftOrRight) {
					long nestedSetsTreeNodeLeft =
						simpleNestedSetsTreeNode.getNestedSetsTreeNodeLeft();

					if (inclusive) {
						if (nestedSetsTreeNodeLeft >= limit) {
							simpleNestedSetsTreeNode.setNestedSetsTreeNodeLeft(
								nestedSetsTreeNodeLeft + delta);
						}
					}
					else {
						if (nestedSetsTreeNodeLeft > limit) {
							simpleNestedSetsTreeNode.setNestedSetsTreeNodeLeft(
								nestedSetsTreeNodeLeft + delta);
						}
					}
				}
				else {
					long nestedSetsTreeNodeRight =
						simpleNestedSetsTreeNode.getNestedSetsTreeNodeRight();

					if (inclusive) {
						if (nestedSetsTreeNodeRight >= limit) {
							simpleNestedSetsTreeNode.setNestedSetsTreeNodeRight(
								nestedSetsTreeNodeRight + delta);
						}
					}
					else {
						if (nestedSetsTreeNodeRight > limit) {
							simpleNestedSetsTreeNode.setNestedSetsTreeNodeRight(
								nestedSetsTreeNodeRight + delta);
						}
					}
				}
			}

			synchronizeSimpleNestedSetsTreeNodes();
		}

		@Override
		protected void doUpdate(
			long nestedSetsTreeNodeScopeId, long delta, long start,
			boolean startInclusive, long end, boolean endInclusive,
			List<SimpleNestedSetsTreeNode> includeList) {

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				if ((includeList != null) &&
					!includeList.contains(simpleNestedSetsTreeNode)) {

					continue;
				}

				long nestedSetsTreeNodeLeft =
					simpleNestedSetsTreeNode._nestedSetsTreeNodeLeft;

				if (isInRange(
						nestedSetsTreeNodeLeft, start, startInclusive, end,
						endInclusive)) {

					simpleNestedSetsTreeNode.setNestedSetsTreeNodeLeft(
						nestedSetsTreeNodeLeft + delta);
				}

				long nestedSetsTreeNodeRight =
					simpleNestedSetsTreeNode._nestedSetsTreeNodeRight;

				if (isInRange(
						nestedSetsTreeNodeRight, start, startInclusive, end,
						endInclusive)) {

					simpleNestedSetsTreeNode.setNestedSetsTreeNodeRight(
						nestedSetsTreeNodeRight + delta);
				}
			}

			synchronizeSimpleNestedSetsTreeNodes();
		}

		@Override
		protected long getMaxNestedSetsTreeNodeRight(
			long nestedSetsTreeNodeScopeId) {

			long maxNestedSetsTreeNodeRight = 0;

			for (SimpleNestedSetsTreeNode simpleNestedSetsTreeNode :
					_simpleNestedSetsTreeNodeList) {

				long nestedSetsTreeNodeRight =
					simpleNestedSetsTreeNode.getNestedSetsTreeNodeRight();

				if (nestedSetsTreeNodeRight > maxNestedSetsTreeNodeRight) {
					maxNestedSetsTreeNodeRight = nestedSetsTreeNodeRight;
				}
			}

			return maxNestedSetsTreeNodeRight + 1;
		}

		protected boolean isInRange(
			long value, long start, boolean startInclusive, long end,
			boolean endInclusive) {

			if (startInclusive) {
				if (value < start) {
					return false;
				}
			}
			else {
				if (value <= start) {
					return false;
				}
			}

			if (endInclusive) {
				if (value > end) {
					return false;
				}
			}
			else {
				if (value >= end) {
					return false;
				}
			}

			return true;
		}

		protected void removeSimpleNestedSetsTreeNode(
			SimpleNestedSetsTreeNode simpleNestedSetsTreeNode) {

			for (int i = 0; i < _simpleNestedSetsTreeNodes.length; i++) {
				if (_simpleNestedSetsTreeNodes[i] == simpleNestedSetsTreeNode) {
					_simpleNestedSetsTreeNodes[i] = null;
				}
			}
		}

		protected void synchronizeSimpleNestedSetsTreeNodes() {
			for (int i = 0; i < _simpleNestedSetsTreeNodes.length; i++) {
				SimpleNestedSetsTreeNode simpleNestedSetsTreeNode =
					_simpleNestedSetsTreeNodes[i];

				if (simpleNestedSetsTreeNode == null) {
					continue;
				}

				int index = _simpleNestedSetsTreeNodeList.indexOf(
					simpleNestedSetsTreeNode);

				if (index < 0) {
					continue;
				}

				simpleNestedSetsTreeNode = _simpleNestedSetsTreeNodeList.get(
					index);

				_simpleNestedSetsTreeNodes[i] =
					simpleNestedSetsTreeNode.clone();
			}
		}

		private final List<SimpleNestedSetsTreeNode>
			_simpleNestedSetsTreeNodeList = new ArrayList<>();

	}

}