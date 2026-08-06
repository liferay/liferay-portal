import * as data from 'test/data';
import mockStore, {toRD} from 'test/mock-store';
import React from 'react';
import {act, renderHook} from '@testing-library/react';
import {actionTypes} from 'shared/actions/users';
import {DEFAULT_LANGUAGE_ID, DEFAULT_LOCALE} from 'shared/util/locale';
import {fromJS} from 'immutable';
import {LanguageIds} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {useLanguageId, useLocale} from 'shared/hooks/useLocale';
import {User} from 'shared/util/records';

jest.unmock('react-dom');

function buildInitialState(
	currentUserId: string,
	users: {[id: string]: string | null}
) {
	const usersState: Record<string, unknown> = {};

	Object.keys(users).forEach((id) => {
		usersState[id] = toRD(
			new User(data.mockUser(Number(id), {languageId: users[id]}))
		);
	});

	return fromJS({
		currentUser: toRD(currentUserId),
		users: usersState,
	});
}

function renderWithStore<T>(
	useHook: () => T,
	store: ReturnType<typeof mockStore>
) {
	return renderHook(useHook, {
		wrapper: ({children}) => <Provider store={store}>{children}</Provider>,
	});
}

describe('useLocale', () => {
	it('wires the current user languageId into resolveLocale', () => {
		const store = mockStore(
			buildInitialState('1', {1: LanguageIds.Portuguese})
		);
		const {result} = renderWithStore(useLocale, store);

		expect(result.current).toBe('pt-BR');
	});

	it('falls back to the default locale when the current user has no languageId set', () => {
		const store = mockStore(buildInitialState('1', {1: null}));
		const {result} = renderWithStore(useLocale, store);

		expect(result.current).toBe(DEFAULT_LOCALE);
	});

	it('re-renders with the new locale when the language preference changes, with no reload involved', () => {
		const store = mockStore(
			buildInitialState('1', {
				1: LanguageIds.English,
				2: LanguageIds.Portuguese,
			})
		);
		const {result} = renderWithStore(useLocale, store);

		expect(result.current).toBe('en-US');

		act(() => {
			store.dispatch({
				payload: {result: '2'},
				type: actionTypes.FETCH_CURRENT_USER_SUCCESS,
			});
		});

		expect(result.current).toBe('pt-BR');
	});
});

describe('useLanguageId', () => {
	it('wires the current user languageId into resolveLanguageId', () => {
		const store = mockStore(
			buildInitialState('1', {1: LanguageIds.Portuguese})
		);
		const {result} = renderWithStore(useLanguageId, store);

		expect(result.current).toBe(LanguageIds.Portuguese);
	});

	it('falls back to the default language id when the current user has no languageId set', () => {
		const store = mockStore(buildInitialState('1', {1: null}));
		const {result} = renderWithStore(useLanguageId, store);

		expect(result.current).toBe(DEFAULT_LANGUAGE_ID);
	});
});
