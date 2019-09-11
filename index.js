import { NativeModules } from 'react-native';

const { LocalApi } = NativeModules;

export default {
  apiRequest: async (url, method, params = {}, body = {}, setCookie = false) => {
    const paramKeys = Object.keys(params);

    if (paramKeys) {
      url += '?';
      paramKeys.forEach((key, index) => {
        url += index > 0 ? '&' : '';
        url += `${key}=${params[key]}`;
      });
      console.log(url);
    }
    try {
      const response = await LocalApi.apiRequest(url, method, body, setCookie);
      const parsedResponse = JSON.parse(response);
      return parsedResponse;
    } catch (error) {
      throw new Error(error);
    }
  },
  clearCookies: LocalApi.clearCookies,
  pinCertificate: LocalApi.pinCertificate,
  setTimeout: LocalApi.setTimeout,
};
