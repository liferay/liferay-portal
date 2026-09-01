/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.query.function.score;

import co.elastic.clients.elasticsearch._types.query_dsl.DecayFunction;
import co.elastic.clients.elasticsearch._types.query_dsl.DecayPlacement;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore.Builder.ContainerBuilder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.UntypedDecayFunction;

import com.liferay.portal.search.elasticsearch8.internal.script.ScriptTranslator;
import com.liferay.portal.search.elasticsearch8.internal.util.SetterUtil;
import com.liferay.portal.search.query.MultiValueMode;
import com.liferay.portal.search.query.function.score.ExponentialDecayScoreFunction;
import com.liferay.portal.search.query.function.score.FieldValueFactorScoreFunction;
import com.liferay.portal.search.query.function.score.GaussianDecayScoreFunction;
import com.liferay.portal.search.query.function.score.LinearDecayScoreFunction;
import com.liferay.portal.search.query.function.score.RandomScoreFunction;
import com.liferay.portal.search.query.function.score.ScoreFunction;
import com.liferay.portal.search.query.function.score.ScoreFunctionTranslator;
import com.liferay.portal.search.query.function.score.ScriptScoreFunction;
import com.liferay.portal.search.query.function.score.WeightScoreFunction;

/**
 * @author Michael C. Han
 */
public class ElasticsearchScoreFunctionTranslator
	implements ScoreFunctionTranslator<ContainerBuilder> {

	@Override
	public ContainerBuilder translate(
		ExponentialDecayScoreFunction exponentialDecayScoreFunction) {

		FunctionScore.Builder functionScoreBuilder =
			new FunctionScore.Builder();

		DecayFunction.Builder decayFunctionBuilder =
			FunctionScoreBuilders.exp();

		UntypedDecayFunction.Builder untypedDecayFunctionBuilder =
			new UntypedDecayFunction.Builder();

		untypedDecayFunctionBuilder.field(
			exponentialDecayScoreFunction.getField());

		untypedDecayFunctionBuilder.multiValueMode(
			_translateMultiValueMode(
				exponentialDecayScoreFunction.getMultiValueMode()));

		DecayPlacement.Builder decayPlacementBuilder =
			new DecayPlacement.Builder();

		SetterUtil.setNotNullDouble(
			decayPlacementBuilder::decay,
			exponentialDecayScoreFunction.getDecay());

		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::offset,
			exponentialDecayScoreFunction.getOffset());
		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::origin,
			exponentialDecayScoreFunction.getOrigin());
		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::scale,
			exponentialDecayScoreFunction.getScale());

		untypedDecayFunctionBuilder.placement(decayPlacementBuilder.build());

		SetterUtil.setNotNullFloatAsDouble(
			functionScoreBuilder::weight,
			exponentialDecayScoreFunction.getWeight());

		decayFunctionBuilder.untyped(untypedDecayFunctionBuilder.build());

		return functionScoreBuilder.exp(decayFunctionBuilder.build());
	}

	@Override
	public ContainerBuilder translate(
		FieldValueFactorScoreFunction fieldValueFactorScoreFunction) {

		FunctionScore.Builder functionScoreBuilder =
			new FunctionScore.Builder();

		co.elastic.clients.elasticsearch._types.query_dsl.
			FieldValueFactorScoreFunction.Builder
				fieldValueFactorScoreFunctionBuilder =
					FunctionScoreBuilders.fieldValueFactor();

		SetterUtil.setNotNullFloatAsDouble(
			fieldValueFactorScoreFunctionBuilder::factor,
			fieldValueFactorScoreFunction.getFactor());

		fieldValueFactorScoreFunctionBuilder.field(
			fieldValueFactorScoreFunction.getField());

		SetterUtil.setNotNullDouble(
			fieldValueFactorScoreFunctionBuilder::missing,
			fieldValueFactorScoreFunction.getMissing());

		if (fieldValueFactorScoreFunction.getModifier() != null) {
			fieldValueFactorScoreFunctionBuilder.modifier(
				_translateModifier(
					fieldValueFactorScoreFunction.getModifier()));
		}

		SetterUtil.setNotNullFloatAsDouble(
			functionScoreBuilder::weight,
			fieldValueFactorScoreFunction.getWeight());

		return functionScoreBuilder.fieldValueFactor(
			fieldValueFactorScoreFunctionBuilder.build());
	}

	@Override
	public ContainerBuilder translate(
		GaussianDecayScoreFunction gaussianDecayScoreFunction) {

		FunctionScore.Builder functionScoreBuilder =
			new FunctionScore.Builder();

		DecayFunction.Builder decayFunctionBuilder =
			FunctionScoreBuilders.gauss();

		UntypedDecayFunction.Builder untypedDecayFunctionBuilder =
			new UntypedDecayFunction.Builder();

		untypedDecayFunctionBuilder.field(
			gaussianDecayScoreFunction.getField());

		untypedDecayFunctionBuilder.multiValueMode(
			_translateMultiValueMode(
				gaussianDecayScoreFunction.getMultiValueMode()));

		DecayPlacement.Builder decayPlacementBuilder =
			new DecayPlacement.Builder();

		SetterUtil.setNotNullDouble(
			decayPlacementBuilder::decay,
			gaussianDecayScoreFunction.getDecay());

		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::offset,
			gaussianDecayScoreFunction.getOffset());
		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::origin,
			gaussianDecayScoreFunction.getOrigin());
		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::scale,
			gaussianDecayScoreFunction.getScale());

		untypedDecayFunctionBuilder.placement(decayPlacementBuilder.build());

		SetterUtil.setNotNullFloatAsDouble(
			functionScoreBuilder::weight,
			gaussianDecayScoreFunction.getWeight());

		decayFunctionBuilder.untyped(untypedDecayFunctionBuilder.build());

		return functionScoreBuilder.gauss(decayFunctionBuilder.build());
	}

	@Override
	public ContainerBuilder translate(
		LinearDecayScoreFunction linearDecayScoreFunction) {

		FunctionScore.Builder functionScoreBuilder =
			new FunctionScore.Builder();

		DecayFunction.Builder decayFunctionBuilder =
			FunctionScoreBuilders.linear();

		UntypedDecayFunction.Builder untypedDecayFunctionBuilder =
			new UntypedDecayFunction.Builder();

		untypedDecayFunctionBuilder.field(linearDecayScoreFunction.getField());

		untypedDecayFunctionBuilder.multiValueMode(
			_translateMultiValueMode(
				linearDecayScoreFunction.getMultiValueMode()));

		DecayPlacement.Builder decayPlacementBuilder =
			new DecayPlacement.Builder();

		SetterUtil.setNotNullDouble(
			decayPlacementBuilder::decay, linearDecayScoreFunction.getDecay());

		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::offset,
			linearDecayScoreFunction.getOffset());
		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::origin,
			linearDecayScoreFunction.getOrigin());
		SetterUtil.setNotNullJsonData(
			decayPlacementBuilder::scale, linearDecayScoreFunction.getScale());

		untypedDecayFunctionBuilder.placement(decayPlacementBuilder.build());

		SetterUtil.setNotNullFloatAsDouble(
			functionScoreBuilder::weight, linearDecayScoreFunction.getWeight());

		decayFunctionBuilder.untyped(untypedDecayFunctionBuilder.build());

		return functionScoreBuilder.linear(decayFunctionBuilder.build());
	}

	@Override
	public ContainerBuilder translate(RandomScoreFunction randomScoreFunction) {
		FunctionScore.Builder functionScoreBuilder =
			new FunctionScore.Builder();

		co.elastic.clients.elasticsearch._types.query_dsl.RandomScoreFunction.
			Builder randomScoreFunctionBuilder =
				FunctionScoreBuilders.randomScore();

		SetterUtil.setNotBlankString(
			randomScoreFunctionBuilder::field, randomScoreFunction.getField());
		SetterUtil.setNotBlankString(
			randomScoreFunctionBuilder::seed,
			String.valueOf(randomScoreFunction.getSeed()));

		SetterUtil.setNotNullFloatAsDouble(
			functionScoreBuilder::weight, randomScoreFunction.getWeight());

		return functionScoreBuilder.randomScore(
			randomScoreFunctionBuilder.build());
	}

	public ContainerBuilder translate(ScoreFunction scoreFunction) {
		if (scoreFunction == null) {
			return null;
		}

		return scoreFunction.accept(this);
	}

	@Override
	public ContainerBuilder translate(ScriptScoreFunction scriptScoreFunction) {
		FunctionScore.Builder functionScoreBuilder =
			new FunctionScore.Builder();

		co.elastic.clients.elasticsearch._types.query_dsl.ScriptScoreFunction.
			Builder scriptScoreFunctionBuilder =
				FunctionScoreBuilders.scriptScore();

		scriptScoreFunctionBuilder.script(
			_scriptTranslator.translate(scriptScoreFunction.getScript()));

		SetterUtil.setNotNullFloatAsDouble(
			functionScoreBuilder::weight, scriptScoreFunction.getWeight());

		return functionScoreBuilder.scriptScore(
			scriptScoreFunctionBuilder.build());
	}

	@Override
	public ContainerBuilder translate(WeightScoreFunction weightScoreFunction) {
		throw new UnsupportedOperationException();
	}

	private FieldValueFactorModifier _translateModifier(
		FieldValueFactorScoreFunction.Modifier modifier) {

		if (modifier == FieldValueFactorScoreFunction.Modifier.LN) {
			return FieldValueFactorModifier.Ln;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.LN1P) {
			return FieldValueFactorModifier.Ln1p;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.LN2P) {
			return FieldValueFactorModifier.Ln2p;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.LOG) {
			return FieldValueFactorModifier.Log;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.LOG1P) {
			return FieldValueFactorModifier.Log1p;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.LOG2P) {
			return FieldValueFactorModifier.Log2p;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.NONE) {
			return FieldValueFactorModifier.None;
		}
		else if (modifier ==
					FieldValueFactorScoreFunction.Modifier.RECIPROCAL) {

			return FieldValueFactorModifier.Reciprocal;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.SQRT) {
			return FieldValueFactorModifier.Sqrt;
		}
		else if (modifier == FieldValueFactorScoreFunction.Modifier.SQUARE) {
			return FieldValueFactorModifier.Square;
		}

		throw new IllegalArgumentException("Invalid modifier " + modifier);
	}

	private co.elastic.clients.elasticsearch._types.query_dsl.MultiValueMode
		_translateMultiValueMode(MultiValueMode multiValueMode) {

		if (multiValueMode == null) {
			return null;
		}

		if (multiValueMode == MultiValueMode.AVG) {
			return co.elastic.clients.elasticsearch._types.query_dsl.
				MultiValueMode.Avg;
		}
		else if (multiValueMode == MultiValueMode.MAX) {
			return co.elastic.clients.elasticsearch._types.query_dsl.
				MultiValueMode.Max;
		}
		else if (multiValueMode == MultiValueMode.MIN) {
			return co.elastic.clients.elasticsearch._types.query_dsl.
				MultiValueMode.Min;
		}
		else if (multiValueMode == MultiValueMode.SUM) {
			return co.elastic.clients.elasticsearch._types.query_dsl.
				MultiValueMode.Sum;
		}

		throw new IllegalArgumentException(
			"Invalid multi value mode " + multiValueMode);
	}

	private final ScriptTranslator _scriptTranslator = new ScriptTranslator();

}