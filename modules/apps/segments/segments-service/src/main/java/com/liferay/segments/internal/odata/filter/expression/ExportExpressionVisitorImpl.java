/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.odata.filter.expression;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.CollectionPropertyExpression;
import com.liferay.portal.odata.filter.expression.ComplexPropertyExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;
import com.liferay.portal.odata.filter.expression.PropertyExpression;
import com.liferay.portal.odata.filter.expression.UnaryExpression;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizerRegistry;
import com.liferay.segments.internal.odata.entity.EntityModelFieldMapperUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eduardo García
 */
public class ExportExpressionVisitorImpl implements ExpressionVisitor<Object> {

	public ExportExpressionVisitorImpl(
		PortletDataContext portletDataContext, StagedModel stagedModel,
		EntityModel entityModel,
		SegmentsFieldCustomizerRegistry segmentsFieldCustomizerRegistry) {

		_portletDataContext = portletDataContext;
		_stagedModel = stagedModel;
		_entityModel = entityModel;
		_segmentsFieldCustomizerRegistry = segmentsFieldCustomizerRegistry;

		_customFieldEntityFields =
			EntityModelFieldMapperUtil.getCustomFieldEntityFields(entityModel);
	}

	@Override
	public Void visitBinaryExpressionOperation(
		BinaryExpression.Operation operation, Object left, Object right) {

		if (!Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			return null;
		}

		EntityField entityField = (EntityField)left;

		if (Objects.equals(EntityField.Type.ID, entityField.getType())) {
			_exportEntityFieldIDReferences(entityField, right);
		}
		else if (_customFieldEntityFields.containsKey(entityField.getName())) {
			_exportEntityFieldCustomFieldReferences(entityField);
		}

		return null;
	}

	@Override
	public Object visitCollectionPropertyExpression(
		CollectionPropertyExpression collectionPropertyExpression) {

		return null;
	}

	@Override
	public Object visitComplexPropertyExpression(
		ComplexPropertyExpression complexPropertyExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		ComplexEntityField complexEntityField =
			(ComplexEntityField)entityFieldsMap.get(
				complexPropertyExpression.getName());

		PropertyExpression propertyExpression =
			complexPropertyExpression.getPropertyExpression();

		Map<String, EntityField> complexEntityFieldEntityFieldsMap =
			complexEntityField.getEntityFieldsMap();

		return complexEntityFieldEntityFieldsMap.get(
			propertyExpression.getName());
	}

	@Override
	public Object visitListExpressionOperation(
			ListExpression.Operation operation, Object left,
			List<Object> rights)
		throws ExpressionVisitException {

		if (!Objects.equals(ListExpression.Operation.IN, operation)) {
			return null;
		}

		EntityField entityField = (EntityField)left;

		if (Objects.equals(EntityField.Type.ID, entityField.getType())) {
			for (Object right : rights) {
				_exportEntityFieldIDReferences(entityField, right);
			}
		}

		return null;
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression) {
		return StringUtil.removeChar(
			literalExpression.getText(), CharPool.APOSTROPHE);
	}

	@Override
	public Object visitMemberExpression(MemberExpression memberExpression)
		throws ExpressionVisitException {

		Expression expression = memberExpression.getExpression();

		return expression.accept(this);
	}

	@Override
	public Object visitMethodExpression(
		List<Object> expressions, MethodExpression.Type type) {

		return null;
	}

	@Override
	public Object visitPrimitivePropertyExpression(
		PrimitivePropertyExpression primitivePropertyExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		return entityFieldsMap.get(primitivePropertyExpression.getName());
	}

	@Override
	public Void visitUnaryExpressionOperation(
		UnaryExpression.Operation operation, Object operand) {

		return null;
	}

	private void _exportEntityFieldCustomFieldReferences(
		EntityField entityField) {

		ExpandoColumn expandoColumn =
			EntityModelFieldMapperUtil.getExpandoColumn(entityField.getName());

		if (expandoColumn == null) {
			return;
		}

		Element entityElement = _portletDataContext.getExportDataElement(
			_stagedModel);

		_portletDataContext.addReferenceElement(
			_stagedModel, entityElement, expandoColumn,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY, false);
	}

	private void _exportEntityFieldIDReferences(
		EntityField entityField, Object value) {

		if (!Objects.equals(EntityField.Type.ID, entityField.getType())) {
			return;
		}

		SegmentsFieldCustomizer segmentsFieldCustomizer =
			_segmentsFieldCustomizerRegistry.getSegmentsFieldCustomizer(
				_entityModel.getName(), entityField.getName());

		if (segmentsFieldCustomizer == null) {
			return;
		}

		ClassedModel classedModel = segmentsFieldCustomizer.getClassedModel(
			(String)value);

		if (classedModel == null) {
			return;
		}

		try {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				_portletDataContext, _stagedModel, (StagedModel)classedModel,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to export classed model " +
						classedModel.getModelClassName(),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportExpressionVisitorImpl.class);

	private final Map<String, EntityField> _customFieldEntityFields;
	private final EntityModel _entityModel;
	private final PortletDataContext _portletDataContext;
	private final SegmentsFieldCustomizerRegistry
		_segmentsFieldCustomizerRegistry;
	private final StagedModel _stagedModel;

}