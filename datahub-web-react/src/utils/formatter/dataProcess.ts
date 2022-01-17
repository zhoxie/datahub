const prefixLink = 'https://wwwin.cisco.com/dir/photo/std/';

export const getUserAvatar = (userName: any, defaultLink = '') => {
    if (!userName) {
        return defaultLink;
    }
    return `${prefixLink}${userName}.jpg`;
};
