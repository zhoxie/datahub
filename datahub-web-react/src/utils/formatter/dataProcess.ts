const prefixLink = 'https://wwwin.cisco.com/dir/photo/std/';
const largePrefixLink = 'https://wwwin.cisco.com/dir/photo/zoom/';

export const getUserAvatar = (userName: any, defaultLink = '') => {
    if (!userName) {
        return defaultLink;
    }
    return `${prefixLink}${userName}.jpg`;
};

export const getLargeUserAvatar = (userName: any, defaultLink = '') => {
    if (!userName) {
        return defaultLink;
    }
    return `${largePrefixLink}${userName}.jpg`;
};
