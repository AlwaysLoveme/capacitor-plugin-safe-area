import {StatusBar, Style} from '@capacitor/status-bar';
import { SafeArea } from 'capacitor-plugin-safe-area';
import vconsole from 'vconsole';
import { createApp } from 'vue'


import './style.css'
import App from './App.vue'

new vconsole()

StatusBar.setOverlaysWebView({
  overlay: true
}).then();
StatusBar.setStyle({
  style: Style.Light
}).then()

SafeArea.getSafeAreaInsets().then(({ insets }) => {
  console.log('insets', insets);
  for (const [key, value] of Object.entries(insets)) {
    document.documentElement.style.setProperty(`--safe-area-inset-${key}`, value + 'px');
  }
});
SafeArea.addListener('safeAreaChanged', (data) => {
  const { insets } = data;
  console.log('insets changed', insets);
  for (const [key, value] of Object.entries(insets)) {
    document.documentElement.style.setProperty(`--safe-area-inset-${key}`, `${value}px`);
  }
});

createApp(App).mount('#app')
