import React from 'react';
import {autoCancel, autoCancelWith, hasRequest} from '../request-decorator';
import {fireEvent, render} from '@testing-library/react';

describe('request-decorator', () => {
	describe('autoCancel', () => {
		it('should cancel the request if the same request was made again', () => {
			expect.assertions(2);

			const cancel = jest.fn();

			class TestAutoCancel extends React.Component {
				@autoCancel
				handleClick() {
					return {cancel};
				}

				render() {
					return (
						<button onClick={this.handleClick}>{'click me'}</button>
					);
				}
			}

			const {getByText} = render(<TestAutoCancel />);

			fireEvent.click(getByText('click me'));

			expect(cancel).not.toBeCalled();

			fireEvent.click(getByText('click me'));

			expect(cancel).toBeCalled();
		});

		it('should not cancel the request if cancel is false', () => {
			expect.assertions(2);

			const cancel = jest.fn();

			class TestAutoCancel extends React.Component {
				@autoCancelWith(false)
				handleClick() {
					return {cancel};
				}

				render() {
					return (
						<button onClick={this.handleClick}>{'click me'}</button>
					);
				}
			}

			const {getByText} = render(<TestAutoCancel />);

			fireEvent.click(getByText('click me'));

			expect(cancel).not.toBeCalled();

			fireEvent.click(getByText('click me'));

			expect(cancel).not.toBeCalled();
		});
	});

	describe('hasRequest', () => {
		it('should cancel the requests on the disposal of the component', () => {
			expect.assertions(2);

			const cancel = jest.fn();

			@hasRequest
			class TestAutoCancel extends React.Component {
				@autoCancel
				handleClick() {
					return {cancel};
				}

				render() {
					return (
						<button onClick={this.handleClick}>{'click me'}</button>
					);
				}
			}

			const {getByText, unmount} = render(<TestAutoCancel />);

			fireEvent.click(getByText('click me'));

			expect(cancel).not.toBeCalled();

			unmount();

			expect(cancel).toBeCalled();
		});
	});
});
