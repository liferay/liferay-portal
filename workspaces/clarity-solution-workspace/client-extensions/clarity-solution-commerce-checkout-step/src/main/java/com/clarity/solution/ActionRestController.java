/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarity.solution;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;

import org.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Mumen Tayyem
 */
@RequestMapping("/action")
@RestController
public class ActionRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		JSONObject jsonObject = new JSONObject(json);

		for (String key : jsonObject.keySet()) {
			if (!key.contains("item_")) {
				continue;
			}

			patch(
				"Bearer " + jwt.getTokenValue(),
				new JSONObject(
				).put(
					"shippingAddressId", jsonObject.getString(key)
				).toString(),
				UriComponentsBuilder.fromPath(
					"/o/headless-commerce-delivery-cart/v1.0/cart-items/" +
						key.split("item_")[1]
				).build(
				).toUri());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}