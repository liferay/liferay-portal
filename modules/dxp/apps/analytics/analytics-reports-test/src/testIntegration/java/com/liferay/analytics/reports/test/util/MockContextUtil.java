/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.test.util;

import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.info.item.provider.AnalyticsReportsInfoItemObjectProvider;
import com.liferay.analytics.reports.test.MockObject;
import com.liferay.analytics.reports.test.MockSuperClassObject;
import com.liferay.analytics.reports.test.analytics.reports.info.item.MockObjectAnalyticsReportsInfoItem;
import com.liferay.analytics.reports.test.analytics.reports.info.item.MockSuperClassObjectAnalyticsReportsInfoItem;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.function.UnsafeRunnable;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
public class MockContextUtil {

	public static void testWithMockContext(
			MockContext mockContext, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(MockContextUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		try {
			serviceRegistrations.add(
				bundleContext.registerService(
					AnalyticsReportsInfoItemObjectProvider.class,
					new AnalyticsReportsInfoItemObjectProvider<MockObject>() {

						@Override
						public MockObject getAnalyticsReportsInfoItemObject(
							InfoItemReference infoItemReference) {

							return new MockObject();
						}

						@Override
						public String getClassName() {
							return MockObject.class.getName();
						}

					},
					null));
			serviceRegistrations.add(
				bundleContext.registerService(
					(Class<AnalyticsReportsInfoItem<MockObject>>)
						(Class<?>)AnalyticsReportsInfoItem.class,
					mockContext.getMockObjectAnalyticsReportsInfoItem(), null));
			serviceRegistrations.add(
				bundleContext.registerService(
					AnalyticsReportsInfoItemObjectProvider.class,
					new AnalyticsReportsInfoItemObjectProvider
						<MockSuperClassObject>() {

						@Override
						public MockSuperClassObject
							getAnalyticsReportsInfoItemObject(
								InfoItemReference infoItemReference) {

							return new MockSuperClassObject();
						}

						@Override
						public String getClassName() {
							return MockSuperClassObject.class.getName();
						}

					},
					null));
			serviceRegistrations.add(
				bundleContext.registerService(
					(Class<AnalyticsReportsInfoItem<MockSuperClassObject>>)
						(Class<?>)AnalyticsReportsInfoItem.class,
					mockContext.
						getMockSuperClassObjectAnalyticsReportsInfoItem(),
					null));
			unsafeRunnable.run();
		}
		finally {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		}
	}

	public static class MockContext {

		public static Builder builder() {
			return new Builder();
		}

		public AnalyticsReportsInfoItem<MockObject>
			getMockObjectAnalyticsReportsInfoItem() {

			return _mockObjectAnalyticsReportsInfoItem;
		}

		public AnalyticsReportsInfoItem<MockSuperClassObject>
			getMockSuperClassObjectAnalyticsReportsInfoItem() {

			return _mockSuperClassObjectAnalyticsReportsInfoItem;
		}

		public static class Builder {

			public MockContext build() {
				return new MockContext(
					_mockObjectAnalyticsReportsInfoItem,
					_mockSuperClassObjectAnalyticsReportsInfoItem);
			}

			public Builder mockObjectAnalyticsReportsInfoItem(
				AnalyticsReportsInfoItem<MockObject>
					mockObjectAnalyticsReportsInfoItem) {

				_mockObjectAnalyticsReportsInfoItem =
					mockObjectAnalyticsReportsInfoItem;

				return this;
			}

			public Builder mockSuperClassObjectAnalyticsReportsInfoItem(
				AnalyticsReportsInfoItem<MockSuperClassObject>
					mockSuperClassObjectAnalyticsReportsInfoItem) {

				_mockSuperClassObjectAnalyticsReportsInfoItem =
					mockSuperClassObjectAnalyticsReportsInfoItem;

				return this;
			}

			private AnalyticsReportsInfoItem<MockObject>
				_mockObjectAnalyticsReportsInfoItem;
			private AnalyticsReportsInfoItem<MockSuperClassObject>
				_mockSuperClassObjectAnalyticsReportsInfoItem;

		}

		private MockContext(
			AnalyticsReportsInfoItem<MockObject>
				mockObjectAnalyticsReportsInfoItem,
			AnalyticsReportsInfoItem<MockSuperClassObject>
				mockSuperClassObjectAnalyticsReportsInfoItem) {

			if (mockObjectAnalyticsReportsInfoItem == null) {
				_mockObjectAnalyticsReportsInfoItem =
					MockObjectAnalyticsReportsInfoItem.builder(
					).build();
			}
			else {
				_mockObjectAnalyticsReportsInfoItem =
					mockObjectAnalyticsReportsInfoItem;
			}

			if (mockSuperClassObjectAnalyticsReportsInfoItem == null) {
				_mockSuperClassObjectAnalyticsReportsInfoItem =
					MockSuperClassObjectAnalyticsReportsInfoItem.builder(
					).build();
			}
			else {
				_mockSuperClassObjectAnalyticsReportsInfoItem =
					mockSuperClassObjectAnalyticsReportsInfoItem;
			}
		}

		private final AnalyticsReportsInfoItem<MockObject>
			_mockObjectAnalyticsReportsInfoItem;
		private final AnalyticsReportsInfoItem<MockSuperClassObject>
			_mockSuperClassObjectAnalyticsReportsInfoItem;

	}

}