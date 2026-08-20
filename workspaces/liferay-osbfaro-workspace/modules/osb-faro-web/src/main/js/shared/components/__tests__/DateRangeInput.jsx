import client from 'shared/apollo/client';
import DateRangeInput from '../DateRangeInput';
import mockStore from 'test/mock-store';
import moment from 'moment';
import React from 'react';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {ApolloProvider} from '@apollo/client';
import {clickFirstSelectableDay} from 'test/helpers';
import {getCustomDateFormat} from 'shared/util/date';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

// Records the props of every ClayDropDown render so a test can invoke the
// callback of an earlier render, the way Clay does when the picker is closed
// from its own trigger. The real component still renders.

const clayDropDownProps = [];

jest.mock('@clayui/drop-down', () => {
	const actual = jest.requireActual('@clayui/drop-down');
	const ClayDropDown = actual.default;

	return {
		...actual,
		__esModule: true,
		default: props => {
			clayDropDownProps.push(props);

			return <ClayDropDown {...props} />;
		}
	};
});

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'})
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '456',
		groupId: '2000',
		query: {
			rangeKey: '30'
		}
	})
}));

const ISO_DATE = /^\d{4}-\d{2}-\d{2}$/;

const WrapperComponent = ({children}) => (
	<ApolloProvider client={client}>
		<Provider store={mockStore()}>
			<MockedProvider mocks={[mockPreferenceReq()]}>
				{children}
			</MockedProvider>
		</Provider>
	</ApolloProvider>
);

describe('DateRangeInput', () => {
	afterEach(() => {
		clayDropDownProps.length = 0;

		cleanup();
	});

	it('renders', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateRangeInput
					value={{
						end: moment(100000000000).format('YYYY-MM-DD'),
						start: moment(0).format('YYYY-MM-DD')
					}}
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-range-input')).toBeInTheDocument();
	});

	it('displays the range in the given display format', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateRangeInput
					displayFormat={getCustomDateFormat()}
					value={{end: '2026-02-10', start: '2026-02-01'}}
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-range-input').value).toBe(
			'Feb 1, 2026 to Feb 10, 2026'
		);
	});

	// The emitted range is read back with `format`, so a `displayFormat` must
	// never leak into it. It used to, which left the picker unable to read its
	// own selection back.

	it('emits the selected date in the value format, not the display format', () => {
		const onChange = jest.fn();

		const {getByTestId} = render(
			<WrapperComponent>
				<DateRangeInput
					displayFormat={getCustomDateFormat()}
					onChange={onChange}
					value={{end: '', start: ''}}
				/>
			</WrapperComponent>
		);

		fireEvent.click(getByTestId('date-range-input'));

		clickFirstSelectableDay();

		expect(onChange).toHaveBeenCalledTimes(1);
		expect(onChange.mock.calls[0][0].start).toMatch(ISO_DATE);
	});

	it('blurs with the current handler when closed through a frozen callback', () => {
		const blurredValues = [];

		const ControlledInput = () => {
			const [value, setValue] = React.useState({end: '', start: ''});

			return (
				<DateRangeInput
					displayFormat={getCustomDateFormat()}
					onBlur={() => blurredValues.push(value)}
					onChange={setValue}
					value={value}
				/>
			);
		};

		render(
			<WrapperComponent>
				<ControlledInput />
			</WrapperComponent>
		);

		const {onActiveChange: closeFromFirstRender} = clayDropDownProps[0];

		clickFirstSelectableDay();

		act(() => closeFromFirstRender(false));

		expect(blurredValues).toHaveLength(1);
		expect(blurredValues[0].start).toMatch(ISO_DATE);
	});
});
