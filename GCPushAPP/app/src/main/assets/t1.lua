function ___MAIN___()
api_tapByImage("/data/user/0/com.zyl.push.app/files/bg.png")
end

function enterTarget()
    api_openAPP("android.settings.SETTINGS")
    api_sleep(1000)
    api_sendTapGesture(999,160)
    api_sleep(1000)
    api_inputString("w")
end