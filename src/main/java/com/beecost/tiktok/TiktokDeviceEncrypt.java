package com.beecost.tiktok;

import cn.banny.auxiliary.Inspector;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.DalvikModule;
import cn.banny.unidbg.linux.android.dvm.DvmClass;
import cn.banny.unidbg.linux.android.dvm.VM;
import cn.banny.unidbg.linux.android.dvm.array.ByteArray;
import cn.banny.unidbg.memory.Memory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import net.dongliu.apk.parser.struct.resource.TypeHeader;

public class TiktokDeviceEncrypt {
    private final DvmClass TTEncryptUtils;
    private final ARMEmulator emulator = new AndroidARMEmulator("com.qidian.dldl.official");
    private final VM vm;

    private TiktokDeviceEncrypt() throws IOException {
        Memory memory = this.emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();
        this.vm = this.emulator.createDalvikVM(null);
        this.vm.setVerbose(true);
        DalvikModule dm = this.vm.loadLibrary(new File("libs/libEncryptor.so"), false);

//        DalvikModule dm = this.vm.loadLibrary(new File("/storage/data/Beecost/bee-hayko-backend/social_platform/service/tiktok/tiktok_device_register/nativates/nativate/example_binaries/libttEncrypt.so"), false);
        dm.callJNI_OnLoad(this.emulator);
        this.TTEncryptUtils = this.vm.resolveClass("com/bytedance/frameworks/encryptor/EncryptorUtil");
//        this.TTEncryptUtils = this.vm.resolveClass("com/bytedance/frameworks/core/encrypt/TTEncryptUtils");
    }


    private void destroy() throws IOException {
        this.emulator.close();
    }

    public static void main(String[] args) throws Exception {
        TiktokDeviceEncrypt test = new TiktokDeviceEncrypt();
//        String data = "{\"_gen_time\":\"" + args[0] + "\",\"header\":{\"access\":\"wifi\",\"aid\":1180,\"app_version\":\"17.8.4\",\"appkey\":\"57bfa27c67e58e7d920028d5\",\"build_serial\":\"41488569\",\"carrier\":\"Viettel Mobile\",\"channel\":\"aweGW\",\"clientudid\":\"bcf9cca3-5644-4828-a79e-c627f6e47da4\",\"cpu_abi\":\"armeabi-v7a\",\"density_dpi\":192,\"device_brand\":\"samsung\",\"device_id\":\"\",\"device_manufacturer\":\"samsung\",\"device_model\":\"SM-G925F\",\"display_density\":\"mdpi\",\"display_name\":\"Obito99\",\"language\":\"vi\",\"manifest_version_code\":170804,\"mc\":\"" + args[3] + "\",\"mcc_mnc\":\"46000\",\"not_request_sender\":0,\"openudid\":\"" + args[2] + "\",\"os\":\"Android\",\"os_api\":22,\"os_version\":\"5.1.1\",\"packageX\":\"com.ss.android.ugc.aweme\",\"region\":\"VN\",\"release_build\":\"2132ca7_20190320\",\"resolution\":\"1280x720\",\"rom\":\"eng.se.infra.20181117.120021\",\"rom_version\":\"samsung-user 5.1.1 20171130.276299 release-keys\",\"sdk_version\":\"2.5.5.8\",\"serial_number\":\"41488569\",\"sig_hash\":\"aea615ab910015038f73c47e45d21466\",\"sim_region\":\"cn\",\"sim_serial_number\":[{\"sim_serial_number\":\"70459549640190877299\"}],\"timezone\":25200,\"tz_name\":\"Asia\\\\/Saigon\",\"tz_offset\":7,\"udid\":\"" + args[1] + "\",\"update_version_code\":170804,\"version_code\":170804},\"magic_tag\":\"ss_app_log\"}";
        String data = "{\"_gen_time\":\"" + args[0] + "\",\"header\":{\"access\":\"wifi\",\"aid\":1128,\"app_version\":\"17.8.4\",\"appkey\":\"57bfa27c67e58e7d920028d5\",\"build_serial\":\"41488569\",\"carrier\":\"Viettel Mobile\",\"channel\":\"aweGW\",\"clientudid\":\"bcf9cca3-5644-4828-a79e-c627f6e47da4\",\"cpu_abi\":\"armeabi-v7a\",\"density_dpi\":192,\"device_brand\":\"samsung\",\"device_id\":\"\",\"device_manufacturer\":\"samsung\",\"device_model\":\"SM-G925F\",\"display_density\":\"mdpi\",\"display_name\":\"Phone\",\"language\":\"vi\",\"manifest_version_code\":170,\"mc\":\"" + args[3] + "\",\"mcc_mnc\":\"46000\",\"not_request_sender\":0,\"openudid\":\"" + args[2] + "\",\"os\":\"Android\",\"os_api\":22,\"os_version\":\"5.1.1\",\"packageX\":\"com.ss.android.ugc.trill\",\"region\":\"VN\",\"release_build\":\"2132ca7_20190320\",\"resolution\":\"1280x720\",\"rom\":\"eng.se.infra.20181117.120021\",\"rom_version\":\"samsung-user 5.1.1 20171130.276299 release-keys\",\"sdk_version\":\"2.5.5.8\",\"serial_number\":\"41488569\",\"sig_hash\":\"aea615ab910015038f73c47e45d21467\",\"sim_region\":\"vn\",\"sim_serial_number\":[{\"sim_serial_number\":\"70459549640190877299\"}],\"timezone\":25200,\"tz_name\":\"Asia\\\\/Saigon\",\"tz_offset\":7,\"udid\":\"" + args[1] + "\",\"update_version_code\":1708,\"version_code\":170},\"magic_tag\":\"ss_app_log\"}";
        byte[] data_encrypt = test.ttEncrypt(data);
        test.destroy();
        Inspector.inspect(data_encrypt, "hello");
    }

    private byte[] ttEncrypt(String str) throws IOException {
        byte[] bArr2 = str.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        gZIPOutputStream.write(bArr2);
        gZIPOutputStream.close();
        byte[] bArr22 = byteArrayOutputStream.toByteArray();
        Number ret = this.TTEncryptUtils.callStaticJniMethod(this.emulator, "ttEncrypt([BI)[B", this.vm.addLocalObject(new ByteArray(bArr22)), bArr22.length);
        long hash = ((long) ret.intValue()) & TypeHeader.NO_ENTRY;
        return ((ByteArray) this.vm.getObject(hash)).getValue();
    }
}
