/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.ImagePersistence;
import com.liferay.portal.kernel.service.persistence.ImageUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class ImagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ImageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Image> iterator = _images.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Image image = _persistence.create(pk);

		Assert.assertNotNull(image);

		Assert.assertEquals(image.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Image newImage = addImage();

		_persistence.remove(newImage);

		Image existingImage = _persistence.fetchByPrimaryKey(
			newImage.getPrimaryKey());

		Assert.assertNull(existingImage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addImage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Image newImage = _persistence.create(pk);

		newImage.setMvccVersion(RandomTestUtil.nextLong());

		newImage.setCtCollectionId(RandomTestUtil.nextLong());

		newImage.setCompanyId(RandomTestUtil.nextLong());

		newImage.setModifiedDate(RandomTestUtil.nextDate());

		newImage.setType(RandomTestUtil.randomString());

		newImage.setHeight(RandomTestUtil.nextInt());

		newImage.setWidth(RandomTestUtil.nextInt());

		newImage.setSize(RandomTestUtil.nextInt());

		_images.add(_persistence.update(newImage));

		Image existingImage = _persistence.findByPrimaryKey(
			newImage.getPrimaryKey());

		Assert.assertEquals(
			existingImage.getMvccVersion(), newImage.getMvccVersion());
		Assert.assertEquals(
			existingImage.getCtCollectionId(), newImage.getCtCollectionId());
		Assert.assertEquals(existingImage.getImageId(), newImage.getImageId());
		Assert.assertEquals(
			existingImage.getCompanyId(), newImage.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingImage.getModifiedDate()),
			Time.getShortTimestamp(newImage.getModifiedDate()));
		Assert.assertEquals(existingImage.getType(), newImage.getType());
		Assert.assertEquals(existingImage.getHeight(), newImage.getHeight());
		Assert.assertEquals(existingImage.getWidth(), newImage.getWidth());
		Assert.assertEquals(existingImage.getSize(), newImage.getSize());
	}

	@Test
	public void testCountByLtSize() throws Exception {
		_persistence.countByLtSize(RandomTestUtil.nextInt());

		_persistence.countByLtSize(0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Image newImage = addImage();

		Image existingImage = _persistence.findByPrimaryKey(
			newImage.getPrimaryKey());

		Assert.assertEquals(existingImage, newImage);
	}

	@Test(expected = NoSuchImageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Image> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Image", "mvccVersion", true, "ctCollectionId", true, "imageId",
			true, "companyId", true, "modifiedDate", true, "type", true,
			"height", true, "width", true, "size", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Image newImage = addImage();

		Image existingImage = _persistence.fetchByPrimaryKey(
			newImage.getPrimaryKey());

		Assert.assertEquals(existingImage, newImage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Image missingImage = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingImage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Image newImage1 = addImage();
		Image newImage2 = addImage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newImage1.getPrimaryKey());
		primaryKeys.add(newImage2.getPrimaryKey());

		Map<Serializable, Image> images = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, images.size());
		Assert.assertEquals(newImage1, images.get(newImage1.getPrimaryKey()));
		Assert.assertEquals(newImage2, images.get(newImage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Image> images = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(images.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Image newImage = addImage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newImage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Image> images = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, images.size());
		Assert.assertEquals(newImage, images.get(newImage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Image> images = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(images.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Image newImage = addImage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newImage.getPrimaryKey());

		Map<Serializable, Image> images = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, images.size());
		Assert.assertEquals(newImage, images.get(newImage.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ImageLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Image>() {

				@Override
				public void performAction(Image image) {
					Assert.assertNotNull(image);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Image newImage = addImage();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Image.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("imageId", newImage.getImageId()));

		List<Image> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Image existingImage = result.get(0);

		Assert.assertEquals(existingImage, newImage);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Image.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("imageId", RandomTestUtil.nextLong()));

		List<Image> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Image newImage = addImage();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Image.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("imageId"));

		Object newImageId = newImage.getImageId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("imageId", new Object[] {newImageId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingImageId = result.get(0);

		Assert.assertEquals(existingImageId, newImageId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Image.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("imageId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"imageId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected Image addImage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Image image = _persistence.create(pk);

		image.setMvccVersion(RandomTestUtil.nextLong());

		image.setCtCollectionId(RandomTestUtil.nextLong());

		image.setCompanyId(RandomTestUtil.nextLong());

		image.setModifiedDate(RandomTestUtil.nextDate());

		image.setType(RandomTestUtil.randomString());

		image.setHeight(RandomTestUtil.nextInt());

		image.setWidth(RandomTestUtil.nextInt());

		image.setSize(RandomTestUtil.nextInt());

		_images.add(_persistence.update(image));

		return image;
	}

	private List<Image> _images = new ArrayList<Image>();
	private ImagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}