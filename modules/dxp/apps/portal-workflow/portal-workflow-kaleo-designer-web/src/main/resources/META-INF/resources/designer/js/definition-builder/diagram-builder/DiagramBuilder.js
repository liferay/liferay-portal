/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';
import {flushSync} from 'react-dom';
import ReactFlow, {
	Background,
	Controls,
	addEdge,
	isEdge,
	isNode,
} from 'react-flow-renderer';
import {v4 as uuidv4} from 'uuid';

import {DefinitionBuilderContext} from '../DefinitionBuilderContext';
import {defaultLanguageId} from '../constants';
import DeserializeUtil from '../source-builder/deserializeUtil';
import {singleEventObserver} from '../util/EventObserver';
import {retrieveDefinitionRequest} from '../util/fetchUtil';
import {DiagramBuilderContextProvider} from './DiagramBuilderContext';
import {nodeTypes} from './components/nodes/utils';
import Sidebar from './components/sidebar/Sidebar';
import {isIdDuplicated} from './components/sidebar/utils';
import edgeTypes from './components/transitions/Edge';
import FloatingConnectionLine from './components/transitions/FloatingConnectionLine';
import getCollidingElements from './util/collisionDetection';
import {detectGroovyOrJavaScript} from './util/detectGroovyOrJavaScript';
import populateAssignmentsData from './util/populateAssignmentsData';
import populateNotificationsData from './util/populateNotificationsData';

let ReactFlowDefault = ReactFlow;

// `react-flow-renderer` provides both a commonjs and ESM version.
// We need this logic here so that both work. Unit tests rely on commonjs and
// our DXP runtime uses ESM.

if (ReactFlowDefault.default) {
	ReactFlowDefault = ReactFlowDefault.default;
}

const deserializeUtil = new DeserializeUtil();

export default function DiagramBuilder() {
	const {
		accountEntryId,
		allowScriptContentToBeExecutedOrIncluded,
		currentEditor,
		definitionName,
		deserialize,
		elements,
		functionActionExecutors,
		hadGroovyOrJavaScriptBefore,
		selectedLanguageId,
		setActive,
		setBlockingError,
		setDefinitionDescription,
		setDefinitionInfo,
		setDefinitionName,
		setDefinitionTitle,
		setDefinitionTitleTranslations,
		setDeserialize,
		setElements,
		setHadGroovyOrJavaScriptBefore,
		setHasGroovyOrJavaScript,
		setShowDefinitionInfo,
		statuses,
		workflowDefinitionVersions,
	} = useContext(DefinitionBuilderContext);
	const reactFlowWrapperRef = useRef(null);
	const [collidingElements, setCollidingElements] = useState(null);
	const [elementRectangle, setElementRectangle] = useState(null);
	const [reactFlowInstance, setReactFlowInstance] = useState(null);
	const [selectedItem, setSelectedItem] = useState(null);
	const [selectedItemNewId, setSelectedItemNewId] = useState(null);
	const [selectedTransitionNewName, setSelectedTransitionNewName] =
		useState(null);
	const [defaultPosition, setDefaultPosition] = useState(null);
	const [scriptedReassignmentTimerIndex, setScriptedReassignmentTimerIndex] =
		useState(null);

	const onConnect = (params) => {
		if (
			elements.filter(
				(element) =>
					isEdge(element) &&
					element.source === params.source &&
					element.target === params.target
			).length
		) {
			return;
		}

		const defaultEdge = !elements.filter(
			(element) =>
				isEdge(element) &&
				element.source === params.source &&
				element.data.defaultEdge
		).length;

		const newEdgeId = uuidv4();

		const newEdge = {
			...params,
			arrowHeadType: 'arrowclosed',
			data: {
				defaultEdge,
				label: {
					[defaultLanguageId]:
						Liferay.Language.get('transition-label'),
				},
				name: newEdgeId,
			},
			id: newEdgeId,
			type: 'transition',
		};

		setElements((previousElements) => addEdge(newEdge, previousElements));
		setSelectedItem(newEdge);
	};

	const onConnectEnd = () => {
		singleEventObserver.notify('handle-connect-end', true);
	};

	const onConnectStart = (_, {nodeId}) => {
		singleEventObserver.notify('handle-connect-start', nodeId);
	};

	const onDragOver = (event) => {
		const reactFlowBounds =
			reactFlowWrapperRef.current.getBoundingClientRect();

		const position = reactFlowInstance.project({
			x:
				event.clientX -
				reactFlowBounds.left -
				elementRectangle.mouseXInRectangle,
			y:
				event.clientY -
				reactFlowBounds.top -
				elementRectangle.mouseYInRectangle,
		});

		setCollidingElements(
			getCollidingElements(elements, elementRectangle, position)
		);

		event.preventDefault();

		event.dataTransfer.dropEffect = 'move';
	};

	const onDrop = useCallback(
		(event) => {
			const reactFlowBounds =
				reactFlowWrapperRef.current.getBoundingClientRect();

			const position = reactFlowInstance.project({
				x:
					event.clientX -
					reactFlowBounds.left -
					elementRectangle.mouseXInRectangle,
				y:
					event.clientY -
					reactFlowBounds.top -
					elementRectangle.mouseYInRectangle,
			});

			if (
				!getCollidingElements(elements, elementRectangle, position)
					.length
			) {
				event.preventDefault();

				const type = event.dataTransfer.getData(
					'application/reactflow'
				);

				const newNode = {
					data: {
						newNode: true,
					},
					id: uuidv4(),
					position,
					type,
				};

				setElements((elements) => elements.concat(newNode));
			}
			setCollidingElements(null);
		},
		[elements, elementRectangle, reactFlowInstance, setElements]
	);

	const onLoad = (reactFlowInstance) => {
		reactFlowInstance.fitView({maxZoom: 1});
		setReactFlowInstance(reactFlowInstance);
	};

	const onNodeDrag = (event, node) => {
		const reactFlowBounds =
			reactFlowWrapperRef.current.getBoundingClientRect();

		const position = reactFlowInstance.project({
			x:
				event.clientX -
				reactFlowBounds.left -
				elementRectangle.mouseXInRectangle,
			y:
				event.clientY -
				reactFlowBounds.top -
				elementRectangle.mouseYInRectangle,
		});

		const filteredElements = elements.filter(
			(element) => element.id !== node.id
		);

		setCollidingElements(
			getCollidingElements(filteredElements, elementRectangle, position)
		);
	};

	const onNodeDragStart = (event) => {
		const elementRectangle = event.currentTarget.getBoundingClientRect();
		const reactFlowBounds =
			reactFlowWrapperRef.current.getBoundingClientRect();

		const position = reactFlowInstance.project({
			x: elementRectangle.left - reactFlowBounds.left,
			y: elementRectangle.top - reactFlowBounds.top,
		});

		setDefaultPosition(position);

		setElementRectangle({
			mouseXInRectangle: event.clientX - elementRectangle.left,
			mouseYInRectangle: event.clientY - elementRectangle.top,
			rectangleHeight: elementRectangle.height,
			rectangleWidth: elementRectangle.width,
		});
	};

	const onNodeDragStop = (event, node) => {
		const reactFlowBounds =
			reactFlowWrapperRef.current.getBoundingClientRect();

		const position = reactFlowInstance.project({
			x:
				event.clientX -
				reactFlowBounds.left -
				elementRectangle.mouseXInRectangle,
			y:
				event.clientY -
				reactFlowBounds.top -
				elementRectangle.mouseYInRectangle,
		});

		flushSync(() => {
			setElements((elements) =>
				elements.map((element) => {
					if (element.id === node.id) {
						element = {
							...element,
							position,
						};
					}

					return element;
				})
			);
		});

		const filteredElements = elements.filter(
			(element) => element.id !== node.id
		);

		if (
			getCollidingElements(filteredElements, elementRectangle, position)
				.length
		) {
			setElements((elements) =>
				elements.map((element) => {
					if (element.id === node.id) {
						element.position = defaultPosition;
					}

					return element;
				})
			);

			setCollidingElements(null);
		}
	};

	useEffect(() => {
		if (
			selectedItem &&
			(selectedLanguageId
				? selectedItem.data.label[selectedLanguageId] !== ''
				: selectedItem.data.label[defaultLanguageId] !== '')
		) {
			setElements((elements) =>
				elements.map((element) => {
					if (element.id === selectedItem.id) {
						element = {
							...element,
							data: {
								...element.data,
								...selectedItem.data,
							},
						};
					}

					return element;
				})
			);
		}

		setShowDefinitionInfo(false);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedItem]);

	useEffect(() => {
		if (selectedItem) {
			if (
				isNode(selectedItem) &&
				selectedItemNewId &&
				selectedItemNewId.trim() !== '' &&
				!isIdDuplicated(elements, selectedItemNewId.trim())
			) {
				setElements((elements) =>
					elements.map((element) => {
						if (element.id === selectedItem.id) {
							element = {
								...element,
								id: selectedItemNewId,
							};

							setSelectedItemNewId(null);

							setSelectedItem(element);
						}
						else if (isEdge(element)) {
							element = {
								...element,
								...(selectedItem.id === element.source && {
									source: selectedItemNewId,
								}),
								...(selectedItem.id === element.target && {
									target: selectedItemNewId,
								}),
							};
						}

						return element;
					})
				);
			}
			else if (isEdge(selectedItem) && selectedTransitionNewName) {
				const updatedTransition = {
					...selectedItem,
					data: {
						...selectedItem.data,
						name: selectedTransitionNewName,
					},
				};

				setSelectedTransitionNewName(null);

				setSelectedItem(updatedTransition);

				setElements((elements) =>
					elements.map((element) => {
						if (element.id === selectedItem.id) {
							return updatedTransition;
						}

						return element;
					})
				);
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedItem, selectedItemNewId, selectedTransitionNewName]);

	useEffect(() => {
		if (deserialize && currentEditor) {
			const xmlDefinition = currentEditor.getData();

			deserializeUtil.updateXMLDefinition(xmlDefinition);

			const elements = deserializeUtil.getElements();

			const metadata = deserializeUtil.getMetadata();

			setDefinitionDescription(metadata.description);
			setDefinitionName(metadata.name);

			setElements(elements);

			if (!allowScriptContentToBeExecutedOrIncluded) {
				const hasGroovyOrJavaScript = detectGroovyOrJavaScript(
					elements,
					setHasGroovyOrJavaScript
				);

				if (hasGroovyOrJavaScript && !hadGroovyOrJavaScriptBefore) {
					setHadGroovyOrJavaScriptBefore(true);
				}
			}

			populateAssignmentsData(
				accountEntryId,
				elements,
				setElements,
				setBlockingError
			);
			populateNotificationsData(accountEntryId, elements, setElements);

			setDeserialize(false);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [currentEditor, deserialize, workflowDefinitionVersions]);

	useEffect(() => {
		if (
			definitionName &&
			workflowDefinitionVersions.length !== 0 &&
			!deserialize
		) {
			retrieveDefinitionRequest(definitionName)
				.then((response) => {
					if (!response.ok) {
						throw new Error(
							sub(
								Liferay.Language.get(
									'failed-to-retrieve-definition-with-name-x'
								),
								definitionName
							)
						);
					}

					return response.json();
				})
				.then(
					({
						active,
						content,
						dateCreated,
						dateModified,
						description,
						name,
						title,
						title_i18n,
						version,
					}) => {
						setActive(active);
						setDefinitionDescription(description);
						setDefinitionInfo({
							dateCreated,
							dateModified,
							totalModifications: version,
						});
						setDefinitionName(name);
						setDefinitionTitle(title);
						setDefinitionTitleTranslations(title_i18n);

						deserializeUtil.updateXMLDefinition(
							encodeURIComponent(content)
						);

						const elements = deserializeUtil.getElements();

						setElements(elements);

						if (!allowScriptContentToBeExecutedOrIncluded) {
							const hasGroovyOrJavaScript =
								detectGroovyOrJavaScript(
									elements,
									setHasGroovyOrJavaScript
								);

							if (
								hasGroovyOrJavaScript &&
								!hadGroovyOrJavaScriptBefore
							) {
								setHadGroovyOrJavaScriptBefore(true);
							}
						}

						populateAssignmentsData(
							accountEntryId,
							elements,
							setElements
						);
						populateNotificationsData(
							accountEntryId,
							elements,
							setElements
						);
					}
				)
				.catch((error) => {
					console.error(error);
				});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [definitionName, workflowDefinitionVersions]);

	const contextProps = {
		collidingElements,
		elementRectangle,
		functionActionExecutors,
		scriptedReassignmentTimerIndex,
		selectedItem,
		selectedItemNewId,
		selectedTransitionNewName,
		setCollidingElements,
		setElementRectangle,
		setScriptedReassignmentTimerIndex,
		setSelectedItem,
		setSelectedItemNewId,
		setSelectedTransitionNewName,
		statuses,
	};

	return (
		<DiagramBuilderContextProvider {...contextProps}>
			<div className="diagram-builder">
				<div className="diagram-area" ref={reactFlowWrapperRef}>
					<ReactFlowDefault
						connectionLineComponent={FloatingConnectionLine}
						edgeTypes={edgeTypes}
						elements={elements}
						minZoom="0.1"
						nodeTypes={nodeTypes}
						onConnect={onConnect}
						onConnectEnd={onConnectEnd}
						onConnectStart={onConnectStart}
						onDragOver={onDragOver}
						onDrop={onDrop}
						onLoad={onLoad}
						onNodeDrag={onNodeDrag}
						onNodeDragStart={onNodeDragStart}
						onNodeDragStop={onNodeDragStop}
					/>

					<Controls showInteractive={false} />

					<Background size={1} />
				</div>

				<Sidebar />
			</div>
		</DiagramBuilderContextProvider>
	);
}
