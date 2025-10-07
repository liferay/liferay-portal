/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.contacts.demo.internal.data.creator;

import com.github.javafaker.Address;
import com.github.javafaker.Bool;
import com.github.javafaker.Commerce;
import com.github.javafaker.Company;
import com.github.javafaker.Country;
import com.github.javafaker.DateAndTime;
import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.github.javafaker.Job;
import com.github.javafaker.LordOfTheRings;
import com.github.javafaker.Name;
import com.github.javafaker.Number;
import com.github.javafaker.PhoneNumber;
import com.github.javafaker.Pokemon;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.util.DateUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Matthew Kong
 */
public abstract class DataCreator {

	public DataCreator() {
	}

	public DataCreator(
		ContactsEngineClient contactsEngineClient, FaroProject faroProject,
		String weDeployServiceName, String collectionName) {

		_contactsEngineClient = contactsEngineClient;
		this.faroProject = faroProject;
		_weDeployServiceName = weDeployServiceName;
		_collectionName = collectionName;
	}

	public Map<String, Object> create(boolean keepInMemory, Object[] params) {
		if (keepInMemory) {
			_memoryCount++;
		}

		return create(params);
	}

	public void create(int count, boolean keepInMemory) {
		create(count, keepInMemory, null);
	}

	public void create(int count, boolean keepInMemory, Object[] params) {
		if (keepInMemory) {
			_memoryCount += count;
		}

		for (int i = 0; i < count; i++) {
			create(params);
		}
	}

	public Map<String, Object> create(Object[] params) {
		Map<String, Object> object = doCreate(params);

		_objects.add(object);

		if ((_objects.size() - _index) >= _BATCH_SIZE) {
			execute();
		}

		return object;
	}

	public void createRandom(
		int maxCount, boolean keepInMemory, Object[] params) {

		create(random.nextInt(maxCount), keepInMemory, params);
	}

	public void execute() {
		if (_objects.isEmpty()) {
			return;
		}

		if (Validator.isNotNull(_collectionName)) {
			addData(_objects.subList(_index, _objects.size()));

			_totalCount += _objects.size() - _index;

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Created ", _objects.size() - _index, StringPool.SPACE,
						_collectionName, " in ", _weDeployServiceName));
			}
		}

		if (_memoryCount == 0) {
			_objects.clear();
		}
		else if (_objects.size() > _memoryCount) {
			_objects = _objects.subList(0, _memoryCount);
		}

		_index = _objects.size();
	}

	public String getClassName() {
		return null;
	}

	public String getClassPKFieldName() {
		return null;
	}

	public List<Map<String, Object>> getObjects() {
		if (_objects.isEmpty() || (_memoryCount == 0)) {
			return Collections.emptyList();
		}

		return _objects.subList(0, _getBound());
	}

	public Map<String, Object> getRandom() {
		if (_objects.isEmpty()) {
			return null;
		}

		return _objects.get(random.nextInt(_getBound()));
	}

	public long getTotalCount() {
		return _totalCount;
	}

	protected static String formatDate(Date date) {
		return DateUtil.formatDate(date, DateUtil.PATTERN_DATE_TIME);
	}

	protected void addData(List<Map<String, Object>> objects) {
		_contactsEngineClient.addData(
			faroProject, _weDeployServiceName, _collectionName, objects);
	}

	protected abstract Map<String, Object> doCreate(Object[] params);

	protected static final Random random = new Random(0);

	protected Address address = _faker.address();
	protected Bool bool = _faker.bool();
	protected Commerce commerce = _faker.commerce();
	protected Company company = _faker.company();
	protected Country country = _faker.country();
	protected DateAndTime dateAndTime = _faker.date();
	protected FaroProject faroProject;
	protected Internet internet = _faker.internet();
	protected Job job = _faker.job();
	protected LordOfTheRings lordOfTheRings = _faker.lordOfTheRings();
	protected Name name = _faker.name();
	protected Number number = _faker.number();
	protected PhoneNumber phoneNumber = _faker.phoneNumber();
	protected Pokemon pokemon = _faker.pokemon();

	private int _getBound() {
		if (_objects.size() <= _memoryCount) {
			return _objects.size();
		}

		return _memoryCount;
	}

	private static final int _BATCH_SIZE = 1000;

	private static final Log _log = LogFactoryUtil.getLog(DataCreator.class);

	private static final Faker _faker = new Faker(new Random(0));

	private String _collectionName;
	private ContactsEngineClient _contactsEngineClient;
	private int _index;
	private int _memoryCount;
	private List<Map<String, Object>> _objects = new ArrayList<>();
	private int _totalCount;
	private String _weDeployServiceName;

}