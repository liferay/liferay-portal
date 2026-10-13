import CanvasEmptyState from './CanvasEmptyState';
import Card from 'shared/components/Card';
import getCN from 'classnames';
import React from 'react';
import {Heading} from '@clayui/core';

const Actions: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({
	children,
	className,
	...otherProps
}) => (
	<div
		className={getCN(
			'align-items-center c-gap-3 canvas-actions d-flex flex-wrap',
			className
		)}
		{...otherProps}
	>
		{children}
	</div>
);

interface ICanvasBodyProps {
	children?: React.ReactNode;
	className?: string;
}

const Body: React.FC<ICanvasBodyProps> = ({children, className}) => (
	<Card.Body className={getCN('canvas-body', className)}>
		{children}
	</Card.Body>
);

interface ICanvasHeaderProps {
	children?: React.ReactNode;
	className?: string;
	hasBody?: boolean;
	title: string;
}

const Header: React.FC<ICanvasHeaderProps> = ({
	children,
	className,
	hasBody = true,
	title,
}) => (
	<Card.Header
		className={getCN(
			'align-items-lg-center align-items-start c-gap-3 canvas-header d-flex flex-column flex-lg-row flex-wrap justify-content-between',
			{'mb-5 pb-0': hasBody},
			className
		)}
	>
		<div className="card-title">
			<Heading fontSize={6} level={2} weight="semi-bold">
				{title}
			</Heading>
		</div>

		{children}
	</Card.Header>
);

interface ICanvasProps {
	children?: React.ReactNode;
	className?: string;
	testId?: string;
}

/**
 * Presentational container for the page editors, built on Card so it shares
 * the spacing and look of the other cards. Each feature fills the slots with
 * its own controls and content, and renders Canvas.EmptyState in the body
 * when it has nothing to show.
 */
const Canvas: React.FC<ICanvasProps> & {
	Actions: typeof Actions;
	Body: typeof Body;
	EmptyState: typeof CanvasEmptyState;
	Header: typeof Header;
} = ({children, className, testId}) => (
	<Card className={getCN('canvas-root', className)} testId={testId}>
		{children}
	</Card>
);

Canvas.Actions = Actions;
Canvas.Body = Body;
Canvas.EmptyState = CanvasEmptyState;
Canvas.Header = Header;

export default Canvas;
