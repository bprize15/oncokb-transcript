import React, { useMemo } from 'react';
import { FaCheckCircle, FaInfoCircle } from 'react-icons/fa';
import { Button } from 'reactstrap';
import { getFirebasePath } from '../util/firebase/firebase-utils';

export interface IGeneStatusBadgeProps {
  hugoSymbol: string;
  isGermline: boolean;
  status: 'Needs Review' | 'Pending Release' | 'Released';
}

export default function GeneStatusBadge({ hugoSymbol, isGermline, status }: IGeneStatusBadgeProps) {
  let color = '';
  const iconPadding = 'pe-1';
  const iconSize = 18;
  let icon = <></>;
  let navigateTo: string | undefined = undefined;

  switch (status) {
    case 'Needs Review':
      color = 'danger';
      icon = <FaInfoCircle size={iconSize} className={iconPadding} />;
      navigateTo = `/curation/${hugoSymbol}/${isGermline ? 'germline' : 'somatic'}/review`;
      break;
    case 'Pending Release':
      color = 'warning';
      icon = <FaInfoCircle size={iconSize} className={iconPadding} />;
      navigateTo = `/curation/${hugoSymbol}/${isGermline ? 'germline' : 'somatic'}`;
      break;
    case 'Released':
      color = 'success';
      icon = <FaCheckCircle style={{ minWidth: 18 }} size={iconSize} className={iconPadding} />;
      break;
    default:
  }

  return (
    <Button
      color={color}
      className="d-flex align-items-center rounded-pill"
      onClick={
        navigateTo
          ? () => {
              if (navigateTo) {
                window.location.href = window.location.href.replace(window.location.pathname, navigateTo);
              }
            }
          : undefined
      }
    >
      {icon}
      {status}
    </Button>
  );
}
