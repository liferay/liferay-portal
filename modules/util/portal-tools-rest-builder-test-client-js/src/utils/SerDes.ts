/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

	import {AssetLibraryTestEntity} from '../models/AssetLibraryTestEntity';
	import {BatchTestEntity} from '../models/BatchTestEntity';
	import {ChildTestEntity1} from '../models/ChildTestEntity1';
	import {ChildTestEntity2} from '../models/ChildTestEntity2';
	import {ChildTestEntity3} from '../models/ChildTestEntity3';
	import {CompanyTestEntity} from '../models/CompanyTestEntity';
	import {EntityModelResourceTestEntity1} from '../models/EntityModelResourceTestEntity1';
	import {EntityModelResourceTestEntity2} from '../models/EntityModelResourceTestEntity2';
	import {EnumTestEntity} from '../models/EnumTestEntity';
	import {ERCAssetLibraryTestEntity} from '../models/ERCAssetLibraryTestEntity';
	import {ERCScopedTestEntity} from '../models/ERCScopedTestEntity';
	import {ERCSiteTestEntity} from '../models/ERCSiteTestEntity';
	import {Facet} from '../models/Facet';
	import {FacetValue} from '../models/FacetValue';
	import {Filter} from '../models/Filter';
	import {JSONMapAttributeTestEntity} from '../models/JSONMapAttributeTestEntity';
	import {MultipartTestEntity} from '../models/MultipartTestEntity';
	import {NestedArrayItemsTestEntity} from '../models/NestedArrayItemsTestEntity';
	import {NestedTestEntity} from '../models/NestedTestEntity';
	import {PageAssetLibraryTestEntity} from '../models/PageAssetLibraryTestEntity';
	import {PageBatchTestEntity} from '../models/PageBatchTestEntity';
	import {PageCompanyTestEntity} from '../models/PageCompanyTestEntity';
	import {PageEntityModelResourceTestEntity1} from '../models/PageEntityModelResourceTestEntity1';
	import {PageERCAssetLibraryTestEntity} from '../models/PageERCAssetLibraryTestEntity';
	import {PageERCScopedTestEntity} from '../models/PageERCScopedTestEntity';
	import {PageERCSiteTestEntity} from '../models/PageERCSiteTestEntity';
	import {PageFilter} from '../models/PageFilter';
	import {PagePermission} from '../models/PagePermission';
	import {PageSchema} from '../models/PageSchema';
	import {PageSiteTestEntity} from '../models/PageSiteTestEntity';
	import {PageSort} from '../models/PageSort';
	import {PageTestEntity} from '../models/PageTestEntity';
	import {Permission} from '../models/Permission';
	import {Schema} from '../models/Schema';
	import {SiteTestEntity} from '../models/SiteTestEntity';
	import {Sort} from '../models/Sort';
	import {StringTestEntity} from '../models/StringTestEntity';
	import {TestEntity} from '../models/TestEntity';
	import {TestEntityAddress} from '../models/TestEntityAddress';
	import {UnreferencedTestEntity} from '../models/UnreferencedTestEntity';

/**
 * @author Alejandro Tardín
 * @generated
 */

function endsWith(str: string, match: string): boolean {
	return (
		str.length >= match.length &&
		str.substring(str.length - match.length) === match
	);
}

function startsWith(str: string, match: string): boolean {
	return str.substring(0, match.length) === match;
}

const arrayPrefix = "Array<";
const arraySuffix = ">";
const mapPrefix = "{ [key: string]: ";
const mapSuffix = "; }";
const nullableSuffix = " | null";
const optionalSuffix = " | undefined";
const primitives = new Set([
	"string",
	"boolean",
	"double",
	"integer",
	"long",
	"float",
	"number",
	"any",
]);
const typeMap: {[index: string]: any} = {
	AssetLibraryTestEntity,
	BatchTestEntity,
	ChildTestEntity1,
	ChildTestEntity2,
	ChildTestEntity3,
	CompanyTestEntity,
	EntityModelResourceTestEntity1,
	EntityModelResourceTestEntity2,
	EnumTestEntity,
	ERCAssetLibraryTestEntity,
	ERCScopedTestEntity,
	ERCSiteTestEntity,
	Facet,
	FacetValue,
	Filter,
	JSONMapAttributeTestEntity,
	MultipartTestEntity,
	NestedArrayItemsTestEntity,
	NestedTestEntity,
	PageAssetLibraryTestEntity,
	PageBatchTestEntity,
	PageCompanyTestEntity,
	PageEntityModelResourceTestEntity1,
	PageERCAssetLibraryTestEntity,
	PageERCScopedTestEntity,
	PageERCSiteTestEntity,
	PageFilter,
	PagePermission,
	PageSchema,
	PageSiteTestEntity,
	PageSort,
	PageTestEntity,
	Permission,
	Schema,
	SiteTestEntity,
	Sort,
	StringTestEntity,
	TestEntity,
	TestEntityAddress,
	UnreferencedTestEntity,
};

export class ObjectSerializer {
	public static deserialize(data: any, type: string): any {
		type = ObjectSerializer.findCorrectType(data, type);
		if (data === undefined) {
			return data;
		}
		else if (primitives.has(type.toLowerCase())) {
			return data;
		}
		else if (endsWith(type, nullableSuffix)) {
			const subType: string = type.slice(0, -nullableSuffix.length);

			return ObjectSerializer.deserialize(data, subType);
		}
		else if (endsWith(type, optionalSuffix)) {
			const subType: string = type.slice(0, -optionalSuffix.length);

			return ObjectSerializer.deserialize(data, subType);
		}
		else if (startsWith(type, arrayPrefix)) {
			const subType: string = type.slice(
				arrayPrefix.length,
				-arraySuffix.length
			);
			const transformedData: any[] = [];
			for (let index = 0; index < data.length; index++) {
				const datum = data[index];
				transformedData.push(
					ObjectSerializer.deserialize(datum, subType)
				);
			}

			return transformedData;
		}
		else if (startsWith(type, mapPrefix)) {
			const subType: string = type.slice(
				mapPrefix.length,
				-mapSuffix.length
			);
			const transformedData: {[key: string]: any} = {};
			for (const key in data) {
				transformedData[key] = ObjectSerializer.deserialize(
					data[key],
					subType
				);
			}

			return transformedData;
		}
		else if (type === "Date") {
			return new Date(data);
		}
		else {
			if (!typeMap[type]) {
				return data;
			}
			const instance = new typeMap[type]();
			const attributeTypes = typeMap[type].getAttributeTypeMap();
			for (let index = 0; index < attributeTypes.length; index++) {
				const attributeType = attributeTypes[index];
				instance[attributeType.name] = ObjectSerializer.deserialize(
					data[attributeType.baseName],
					attributeType.type
				);
			}

			return instance;
		}
	}

	public static findCorrectType(data: any, expectedType: string) {
		if (data === undefined) {
			return expectedType;
		}
		else if (primitives.has(expectedType.toLowerCase())) {
			return expectedType;
		}
		else if (expectedType === "Date") {
			return expectedType;
		}
		else {
			if (!typeMap[expectedType]) {
				return expectedType;
			}

			const discriminatorProperty = typeMap[expectedType].discriminator;
			if (discriminatorProperty === null) {
				return expectedType;
			}
			else {
				if (data[discriminatorProperty]) {
					const discriminatorType = data[discriminatorProperty];
					if (typeMap[discriminatorType]) {
						return discriminatorType;
					}
					else {
						return expectedType;
					}
				}
				else {
					return expectedType;
				}
			}
		}
	}

	public static serialize(data: any, type: string): any {
		if (data === undefined) {
			return data;
		}
		else if (primitives.has(type.toLowerCase())) {
			return data;
		}
		else if (endsWith(type, nullableSuffix)) {
			const subType: string = type.slice(0, -nullableSuffix.length);

			return ObjectSerializer.serialize(data, subType);
		}
		else if (endsWith(type, optionalSuffix)) {
			const subType: string = type.slice(0, -optionalSuffix.length);

			return ObjectSerializer.serialize(data, subType);
		}
		else if (startsWith(type, arrayPrefix)) {
			const subType: string = type.slice(
				arrayPrefix.length,
				-arraySuffix.length
			);
			const transformedData: any[] = [];
			for (let index = 0; index < data.length; index++) {
				const datum = data[index];
				transformedData.push(
					ObjectSerializer.serialize(datum, subType)
				);
			}

			return transformedData;
		}
		else if (startsWith(type, mapPrefix)) {
			const subType: string = type.slice(
				mapPrefix.length,
				-mapSuffix.length
			);
			const transformedData: {[key: string]: any} = {};
			for (const key in data) {
				transformedData[key] = ObjectSerializer.serialize(
					data[key],
					subType
				);
			}

			return transformedData;
		}
		else if (type === "Date") {
			return data.toISOString();
		}
		else {
			if (!typeMap[type]) {
				return data;
			}

			type = this.findCorrectType(data, type);

			const attributeTypes = typeMap[type].getAttributeTypeMap();
			const instance: {[index: string]: any} = {};
			for (let index = 0; index < attributeTypes.length; index++) {
				const attributeType = attributeTypes[index];
				instance[attributeType.baseName] = ObjectSerializer.serialize(
					data[attributeType.name],
					attributeType.type
				);
			}

			return instance;
		}
	}
}