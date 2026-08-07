import React, {
	createContext,
	ReactNode,
	useCallback,
	useContext,
	useMemo,
	useState,
} from 'react';
import {
	buildQueryString,
	ILifecycleFilterValues,
} from '../utils/buildQueryString';
import {LifecycleStages} from 'contacts/pages/account/utils/constants';

interface ILifecycleFilters extends ILifecycleFilterValues {
	filterString: string;
}

interface ILifecycleContext {
	filters: ILifecycleFilters;
	lifecycleId: string;
	selectStage: (stage: LifecycleStages) => void;
	stageSelectionNonce: number;
	updateFilters: (newFilters: Partial<ILifecycleFilterValues>) => void;
	resetFilters: () => void;
}

const LifecycleContext = createContext<ILifecycleContext>({
	filters: {
		countryFilter: '',
		filterString: '',
		industryFilter: '',
	},
	lifecycleId: '',
	resetFilters: () => {},
	selectStage: () => {},
	stageSelectionNonce: 0,
	updateFilters: () => {},
});

export const useLifecycle = (): ILifecycleContext =>
	useContext(LifecycleContext);

const initialValues: ILifecycleFilterValues = {
	countryFilter: '',
	industryFilter: '',
};

interface ILifecycleContextProviderProps {
	children: ReactNode;
	lifecycleId?: string;
}

export const LifecycleContextProvider = ({
	children,
	lifecycleId = '',
}: ILifecycleContextProviderProps) => {
	const [filterValues, setFilterValues] =
		useState<ILifecycleFilterValues>(initialValues);

	const [stageSelectionNonce, setStageSelectionNonce] = useState(0);

	const filters = useMemo<ILifecycleFilters>(
		() => ({
			...filterValues,
			filterString: buildQueryString(filterValues),
		}),
		[filterValues]
	);

	const updateFilters = useCallback(
		(newValues: Partial<ILifecycleFilterValues>) =>
			setFilterValues((prev) => ({...prev, ...newValues})),
		[]
	);

	const selectStage = useCallback((stage: LifecycleStages) => {
		setFilterValues((prev) => ({...prev, lifecycleStageFilter: stage}));
		setStageSelectionNonce((prev) => prev + 1);
	}, []);

	const resetFilters = useCallback(() => setFilterValues(initialValues), []);

	const value = useMemo(
		() => ({
			filters,
			lifecycleId,
			resetFilters,
			selectStage,
			stageSelectionNonce,
			updateFilters,
		}),
		[
			filters,
			lifecycleId,
			resetFilters,
			selectStage,
			stageSelectionNonce,
			updateFilters,
		]
	);

	return (
		<LifecycleContext.Provider value={value}>
			{children}
		</LifecycleContext.Provider>
	);
};
