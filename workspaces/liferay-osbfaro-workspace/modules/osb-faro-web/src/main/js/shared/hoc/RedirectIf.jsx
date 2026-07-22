import React from 'react';
import {isString} from 'lodash';
import {Navigate} from 'react-router-dom';
import {PropTypes} from 'prop-types';

/**
 * routingFn
 * @callback routingFn
 * @param {Object} props Passed props
 * @returns {string|null} Route or null
 */

/**
 * HOC for checking if the user should be rerouted to another page
 * @param {routingFn} routingFn - Function to check whether user should be
 * rerouted. Should return either a route or false to render WrappedComponent
 * @returns {Function} - The new component
 */
export default routingFn => WrappedComponent =>
	class redirectIf extends React.Component {
		state = {
			route: PropTypes.string
		};

		constructor(props) {
			super(props);

			const route = routingFn(props);

			this.state = {
				...this.state,
				route
			};
		}

		render() {
			const {route} = this.state;

			if (isString(route)) {
				return <Navigate to={route} />;
			} else {
				return (
					<WrappedComponent
						{...this.props}
						className={
							this.props.className
								? ` ${this.props.className}`
								: ''
						}
					/>
				);
			}
		}
	};
