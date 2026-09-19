/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the CommerceWishListItem service. Represents a row in the &quot;CommerceWishListItem&quot; database table, with each column mapped to a property of this class.
 *
 * @author Andrea Di Giorgi
 * @see CommerceWishListItemModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.commerce.wish.list.model.impl.CommerceWishListItemImpl"
)
@ProviderType
public interface CommerceWishListItem
	extends CommerceWishListItemModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.commerce.wish.list.model.impl.CommerceWishListItemImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<CommerceWishListItem, Long>
		COMMERCE_WISH_LIST_ITEM_ID_ACCESSOR =
			new Accessor<CommerceWishListItem, Long>() {

				@Override
				public Long get(CommerceWishListItem commerceWishListItem) {
					return commerceWishListItem.getCommerceWishListItemId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<CommerceWishListItem> getTypeClass() {
					return CommerceWishListItem.class;
				}

			};

	public com.liferay.commerce.product.model.CPInstance fetchCPInstance()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.commerce.product.model.CPDefinition getCPDefinition()
		throws com.liferay.portal.kernel.exception.PortalException;

	public com.liferay.commerce.product.model.CProduct getCProduct()
		throws com.liferay.portal.kernel.exception.PortalException;

	public CommerceWishList getCommerceWishList()
		throws com.liferay.portal.kernel.exception.PortalException;

	public boolean isIgnoreSKUCombinations()
		throws com.liferay.portal.kernel.exception.PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1357421532