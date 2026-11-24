import Overlay from '../Overlay';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {PropTypes} from 'prop-types';

describe('Overlay', () => {
	afterEach(cleanup);

	it('should render the trigger', () => {
		render(
			<Overlay
				children={[
					<button key='foo'>{'trigger'}</button>,
					<div key='bar'>{'content'}</div>
				]}
			/>
		);

		expect(document.body).toMatchSnapshot();
	});

	it('should call render content if the overlay content re-renders', () => {
		class TestComponent extends React.Component {
			static propTypes = {
				content: PropTypes.string.isRequired
			};

			render() {
				return (
					<Overlay active>
						<button>{'trigger'}</button>
						<div className='content'>{this.props.content}</div>
					</Overlay>
				);
			}
		}

		const {rerender} = render(<TestComponent content='foo' />);
		rerender(<TestComponent content='bar' />);
		expect(document.body).toMatchSnapshot();
	});

	it('should stay active if a click occurs in a nested overlay', () => {
		class TestComponent extends React.Component {
			render() {
				return (
					<Overlay {...this.props} active>
						<button>{'trigger'}</button>
						<div className='content'>
							<Overlay active ref='nested'>
								<button>{'nested'}</button>
								<div className='nested-content-container'>
									<div className='nested-content' />
								</div>
							</Overlay>
						</div>
					</Overlay>
				);
			}
		}

		const onOutsideClick = jest.fn();
		const {container, getByText} = render(
			<TestComponent onOutsideClick={onOutsideClick} />
		);

		jest.runAllTimers();

		expect(onOutsideClick).not.toHaveBeenCalled();

		expect(container).toMatchSnapshot();

		fireEvent.click(getByText('nested'));

		jest.runAllTimers();

		expect(onOutsideClick).not.toHaveBeenCalled();
	});
});
