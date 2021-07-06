package org.linphone.settings;

import org.linphone.LinphoneManager;
import org.linphone.core.AuthInfo;
import org.linphone.core.Core;
import org.linphone.core.ProxyConfig;

public class testpassword extends SettingsFragment {
    private ProxyConfig mProxyConfig;
    private AuthInfo mAuthInfo;
    private int mAccountIndex;

    public void change() {

        Core core = LinphoneManager.getCore();
        //  if (core == null) return;
        String defaultConfig = LinphonePreferences.instance().getDefaultDynamicConfigFile();
        core.loadConfigFromXml(defaultConfig);
        mProxyConfig = core.createProxyConfig();
        mAuthInfo = core.findAuthInfo("UDP", "101", "49.159.128.172");
        mAuthInfo.setPassword("0821555");
    }
}
