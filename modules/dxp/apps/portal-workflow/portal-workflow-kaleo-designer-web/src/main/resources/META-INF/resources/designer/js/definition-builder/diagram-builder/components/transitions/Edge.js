/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useContext, useMemo} from 'react';
import {EdgeText, getBezierPath, useStoreState} from 'react-flow-renderer';

import {DefinitionBuilderContext} from '../../../DefinitionBuilderContext';
import {defaultLanguageId} from '../../../constants';
import {DiagramBuilderContext} from '../../DiagramBuilderContext';
import getBezierEdgeCenter from '../../util/getBezierEdgeCenter';
import MarkerEndDefinition, {getMarkerEndId} from './MarkerEndDefinition';
import {getEdgeParams} from './utils';

function Edge(props) {
	const {
		data: {defaultEdge = true, label},
		id,
		source,
		style = {},
		target,
	} = props;

	const {elements, selectedLanguageId} = useContext(DefinitionBuilderContext);
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	let labelProps = label;

	if (!labelProps || !labelProps[defaultLanguageId]) {
		labelProps = {
			[defaultLanguageId]: Liferay.Language.get('task'),
		};
	}

	let edgeLabel = labelProps[defaultLanguageId];

	if (selectedLanguageId && labelProps[selectedLanguageId]) {
		edgeLabel = labelProps[selectedLanguageId];
	}

	const nodes = useStoreState((state) => state.nodes);

	const sourceNode = useMemo(
		() => nodes.find((node) => node.id === source),
		[source, nodes]
	);
	const targetNode = useMemo(
		() => nodes.find((node) => node.id === target),
		[target, nodes]
	);

	const {sourcePos, sx, sy, targetPos, tx, ty} = getEdgeParams(
		sourceNode,
		targetNode
	);

	const hasCollidingNode = elements.filter(
		(element) => element.source === target && element.target === source
	).length;

	const collidedTransitionIndex = elements.findIndex(
		(element) => element.source === target && element.target === source
	);

	const currentTransitionIndex = elements.findIndex(
		(element) => element.id === id
	);

	let newSourceX = sx;
	let newTargetX = tx;

	if (hasCollidingNode) {
		newSourceX =
			currentTransitionIndex > collidedTransitionIndex
				? newSourceX + 40
				: newSourceX - 40;
		newTargetX =
			currentTransitionIndex > collidedTransitionIndex
				? newTargetX + 40
				: newTargetX - 40;
	}

	const drawn = getBezierPath({
		sourcePosition: sourcePos,
		sourceX: newSourceX,
		sourceY: sy,
		targetPosition: targetPos,
		targetX: newTargetX,
		targetY: ty,
	});

	// eslint-disable-next-line prefer-const
	let [edgeCenterX, edgeCenterY] = getBezierEdgeCenter({
		curvature: 0.25,
		sourcePosition: sourcePos,
		sourceX: newSourceX,
		sourceY: sy,
		targetPosition: targetPos,
		targetX: newTargetX,
		targetY: ty,
	});

	if (hasCollidingNode) {
		edgeCenterY =
			currentTransitionIndex > collidedTransitionIndex
				? edgeCenterY + 21
				: edgeCenterY - 21;
	}

	const selected = selectedItem?.id === id;

	const edgeStyle = {
		...style,
		strokeDasharray: 0,
		strokeWidth: 2,
	};

	if (!defaultEdge) {
		edgeStyle.strokeDasharray = 5;
	}

	return (
		<g className={classNames('react-flow__connection', {selected})}>
			<MarkerEndDefinition edgeId={id} />

			<path
				className="react-flow__edge-path"
				d={drawn}
				id={id}
				markerEnd={`url(#${getMarkerEndId(id)})`}
				style={edgeStyle}
			/>

			<EdgeText
				className="react-flow__edge-text"
				label={edgeLabel?.toUpperCase()}
				labelBgBorderRadius="13px"
				labelBgPadding={[8, 4]}
				labelShowBg={true}
				labelStyle={{fontWeight: 600}}
				onClick={() => setSelectedItem(props)}
				x={edgeCenterX}
				y={edgeCenterY}
			/>
		</g>
	);
}

Edge.propTypes = {
	data: PropTypes.shape({
		defaultEdge: PropTypes.bool,
		label: PropTypes.object,
	}),
	id: PropTypes.string.isRequired,
	source: PropTypes.string,
	sourcePosition: PropTypes.string,
	sourceX: PropTypes.number,
	sourceY: PropTypes.number,
	style: PropTypes.object,
	target: PropTypes.string,
	targetPosition: PropTypes.string,
	targetX: PropTypes.number,
	targetY: PropTypes.number,
};

const edgeTypes = {
	transition: Edge,
};

export default edgeTypes;
