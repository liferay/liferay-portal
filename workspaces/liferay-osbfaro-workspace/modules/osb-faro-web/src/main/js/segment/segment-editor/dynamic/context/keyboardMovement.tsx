import React, {
	createContext,
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';
import {
	addCriterionAtIndex,
	findPropertyByCriterion,
	isCriterionGroup,
} from '../utils/utils';
import {Criteria, Criterion, CriterionGroup} from '../utils/types';
import {getCriterionLabel} from '../utils/getCriterionLabel';
import {getGroupOperatorLabel} from '../criteria-builder/GroupOperator';
import {
	applyMoveTarget,
	getMoveTargets,
	MovePosition,
	MoveTarget,
} from '../utils/keyboardMovement';
import {Property} from 'shared/util/records';
import {
	ReferencedObjectsContext,
	ReferencedProperties,
} from './referencedObjects';
import {sub} from 'shared/util/lang';
import {useAnnouncement} from 'shared/components/condition-chip/AnnounceContext';

export interface MovementSource {
	criterion: Criterion;
	property: Property;
}

interface IKeyboardMovementContext {
	source: MovementSource | null;
	startMovement: ((source: MovementSource) => void) | null;
	target: MoveTarget | null;
}

export const KeyboardMovementContext = createContext<IKeyboardMovementContext>({
	source: null,
	startMovement: null,
	target: null,
});

export const useKeyboardMovement = () => useContext(KeyboardMovementContext);

const POSITION_LABELS: Record<MovePosition, string> = {
	bottom: Liferay.Language.get('bottom'),
	middle: Liferay.Language.get('group'),
	top: Liferay.Language.get('top'),
};

/**
 * Names a drop target the way the canvas names it to a screen reader: a row by
 * its full condition, so two rows with the same attribute can be told apart,
 * and a group by the sentence of its operator.
 */
const getTargetLabel = (
	node: Criteria,
	referencedProperties: ReferencedProperties,
	sequential: boolean
): string => {
	if (isCriterionGroup(node)) {
		return sequential
			? Liferay.Language.get('group')
			: getGroupOperatorLabel(node.conjunctionName);
	}

	return getCriterionLabel(
		node,
		findPropertyByCriterion(node, referencedProperties)
	);
};

interface IKeyboardMovementProviderProps {
	children: React.ReactNode;
	criteria: CriterionGroup | null;
	onChange: (criteria: CriterionGroup) => void;
	sequential: boolean;
}

/**
 * Lets keyboard users drop a condition from the library anywhere on the
 * canvas, like the Audience Builder: Enter picks the condition, the arrow
 * keys move through the drop targets, Enter drops it, and Esc cancels.
 */
export function KeyboardMovementProvider({
	children,
	criteria,
	onChange,
	sequential,
}: IKeyboardMovementProviderProps) {
	const {addProperty, referencedProperties} = useContext(
		ReferencedObjectsContext
	);

	const {announce, announcement} = useAnnouncement();
	const [source, setSource] = useState<MovementSource | null>(null);
	const [targetIndex, setTargetIndex] = useState(0);

	const targets = useMemo(
		() => getMoveTargets(criteria, sequential),
		[criteria, sequential]
	);

	const latestRef = useRef({
		addProperty,
		criteria,
		onChange,
		referencedProperties,
		sequential,
		targets,
	});

	latestRef.current = {
		addProperty,
		criteria,
		onChange,
		referencedProperties,
		sequential,
		targets,
	};

	const drop = useCallback(
		(newCriteria: CriterionGroup, property: Property) => {
			latestRef.current.addProperty?.(property);

			latestRef.current.onChange(newCriteria);

			announce(
				sub(
					Liferay.Language.get('x-was-added-to-the-segment-criteria'),
					[property.label]
				) as string
			);

			setSource(null);
		},
		[announce]
	);

	const startMovement = useCallback(
		(movementSource: MovementSource) => {
			const {criteria, targets} = latestRef.current;

			if (!targets.length) {
				drop(
					addCriterionAtIndex(criteria, 0, movementSource.criterion),
					movementSource.property
				);

				return;
			}

			setSource(movementSource);
			setTargetIndex(targets.length - 1);

			announce(
				sub(
					Liferay.Language.get(
						'use-the-arrow-keys-to-choose-where-to-add-x-and-press-enter-to-confirm-or-escape-to-cancel'
					),
					[movementSource.property.label]
				) as string
			);
		},
		[announce, drop]
	);

	const target = source ? targets[targetIndex] ?? null : null;

	useEffect(() => {
		if (!source) {
			return;
		}

		document
			.querySelector('.keyboard-movement-target')
			?.scrollIntoView?.({behavior: 'smooth', block: 'nearest'});
	}, [source, targetIndex]);

	useEffect(() => {
		if (!source) {
			return;
		}

		let currentIndex = latestRef.current.targets.length - 1;

		const moveTo = (index: number) => {
			const {referencedProperties, sequential, targets} =
				latestRef.current;

			currentIndex = Math.min(Math.max(index, 0), targets.length - 1);

			const {node, position} = targets[currentIndex];

			setTargetIndex(currentIndex);

			announce(
				sub(Liferay.Language.get('targeting-x-of-x'), [
					POSITION_LABELS[position],
					getTargetLabel(node, referencedProperties, sequential),
				]) as string
			);
		};

		const handleKeyDown = (event: KeyboardEvent) => {
			event.preventDefault();
			event.stopPropagation();

			if (event.key === 'ArrowDown') {
				moveTo(currentIndex + 1);
			}
			else if (event.key === 'ArrowUp') {
				moveTo(currentIndex - 1);
			}
			else if (event.key === 'End') {
				moveTo(Infinity);
			}
			else if (event.key === 'Home') {
				moveTo(0);
			}
			else if (event.key === 'Enter' || event.key === ' ') {
				const {criteria, sequential, targets} = latestRef.current;

				drop(
					applyMoveTarget(
						criteria!,
						targets[currentIndex],
						source.criterion,
						sequential
					),
					source.property
				);
			}
			else if (event.key === 'Escape') {
				announce('');
				setSource(null);
			}
		};

		window.addEventListener('keydown', handleKeyDown, true);

		return () => window.removeEventListener('keydown', handleKeyDown, true);
	}, [announce, drop, source]);

	const value = useMemo(
		() => ({source, startMovement, target}),
		[source, startMovement, target]
	);

	return (
		<KeyboardMovementContext.Provider value={value}>
			<div className="sr-only" role="status">
				{announcement}
			</div>

			{children}
		</KeyboardMovementContext.Provider>
	);
}
