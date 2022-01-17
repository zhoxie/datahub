import { Avatar, Tooltip, Image } from 'antd';
import { TooltipPlacement } from 'antd/lib/tooltip';
import React from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

import defaultAvatar from '../../../images/default_avatar.png';
import getAvatarColor from './getAvatarColor';

const AvatarStyled = styled(Avatar)<{ size?: number; $backgroundColor: string }>`
    color: #fff;
    background-color: ${(props) => props.$backgroundColor};
    font-size: ${(props) => (props.size ? `${Math.max(props.size / 2.0, 14)}px` : '14px')} !important;
    margin-right: 4px;
    height: 36px;
    width: 36px;

    .ant-avatar-string {
        text-align: center;
        top: ${(props) => ((props.size || 0) < 24 ? '-4' : '0')}px;
    }
`;

type Props = {
    url?: string;
    photoUrl?: string;
    useDefaultAvatar?: boolean;
    name?: string;
    style?: React.CSSProperties;
    placement?: TooltipPlacement;
    size?: number;
    isGroup?: boolean;
};

export default function CustomAvatar({
    url,
    photoUrl,
    useDefaultAvatar,
    name,
    style,
    placement,
    size,
    isGroup = false,
}: Props) {
    const avatarWithInitial = name ? (
        <AvatarStyled style={style} size={size} $backgroundColor={getAvatarColor(name)}>
            {name.charAt(0).toUpperCase()}
        </AvatarStyled>
    ) : (
        <AvatarStyled src={defaultAvatar} style={style} size={size} $backgroundColor={getAvatarColor(name)} />
    );
    const avatarWithDefault = useDefaultAvatar ? (
        <AvatarStyled src={defaultAvatar} style={style} size={size} $backgroundColor={getAvatarColor(name)} />
    ) : (
        avatarWithInitial
    );
    const avatar =
        photoUrl && photoUrl !== '' ? (
            <AvatarStyled
                src={<Image src={photoUrl} style={style} />}
                size={size}
                $backgroundColor={getAvatarColor(name)}
            />
        ) : (
            avatarWithDefault
        );
    if (!name) {
        return url ? <Link to={url}>{avatar}</Link> : avatar;
    }
    return (
        <Tooltip title={isGroup ? `${name} - Group` : name} placement={placement}>
            {url ? <Link to={url}>{avatar}</Link> : avatar}
        </Tooltip>
    );
}
