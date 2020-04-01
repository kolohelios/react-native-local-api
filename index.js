import { NativeModules } from 'react-native';

const { LocalApi } = NativeModules;

export default {
  apiRequest: async (url, method, params = {}, body = {}, setCookie = false) => new Promise(async (fulfilled, rejected) => {
      const paramKeys = Object.keys(params);

      if (paramKeys.length) {
        url += '?';
        paramKeys.forEach((key, index) => {
          url += index > 0 ? '&' : '';
          url += `${key}=${params[key]}`;
        });
      }
      try {
        const response = await LocalApi.apiRequest(url, method, body, setCookie);
        const parsedResponse = JSON.parse(response);
        fulfilled(parsedResponse);
      } catch (error) {
        rejected(error);
      }
  }),
  clearCookies: LocalApi.clearCookies,
  pinCertificate: LocalApi.pinCertificate,
  setTimeout: LocalApi.setTimeout,
};
