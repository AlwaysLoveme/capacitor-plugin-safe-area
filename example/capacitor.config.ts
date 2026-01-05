import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.example.app',
  appName: 'example',
  webDir: 'dist',
  server: {
    url: 'http://10.200.77.5:8000/',
  },
};

export default config;
