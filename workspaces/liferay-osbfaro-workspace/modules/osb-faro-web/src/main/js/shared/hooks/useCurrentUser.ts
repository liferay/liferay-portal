import {fetchCurrentUser} from 'shared/actions/users';
import {resolveLanguageId, resolveLocale, setLocale} from 'shared/util/locale';
import {setMomentLocale} from 'shared/util/date';
import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import {User} from 'shared/util/records';

/**
 * Used only on the first time on App.tsx to fetch data from backend.
 * To get currentUser, you can use WithCurrentUser (for HOC) or useCurrentUser (for HOOK).
 */
export const useFetchCurrentUser = (initialGroupId: string = '0') => {
	const currentUser = useSelector<any, any>((state) =>
		state.get('currentUser')
	);
	const error = currentUser.get('error');
	const loading = currentUser.get('loading');

	const data = useCurrentUser();

	const dispatch = useDispatch();

	useEffect(() => {
		let groupId = '0';

		if (initialGroupId && initialGroupId !== 'add') {
			groupId = initialGroupId;
		}

		dispatch(fetchCurrentUser(groupId));
	}, [initialGroupId]);

	useEffect(() => {
		setLocale(resolveLocale(data.languageId));
		setMomentLocale(resolveLanguageId(data.languageId));
	}, [data.languageId]);

	return {
		data,
		error,
		loading,
	};
};

/**
 * Get currentUser from redux store.
 */
export const useCurrentUser = (): User => {
	const currentUserId = useSelector<any, any>((state) =>
		state.getIn(['currentUser', 'data'])
	);
	const data: User = useSelector<any, any>((state) =>
		state.getIn(['users', currentUserId, 'data'])
	);

	const newUser = new User({
		emailAddress: '',
		id: '',
		name: '',
		roleName: '',
		status: 1,
	});

	return data || newUser;
};
