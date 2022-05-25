#include <string>
#include <jni.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define IODEV_IOC_MAGIC  'H'
#define IODEV_IOCTL_IDENTITY_UHF_POWER    _IOW(IODEV_IOC_MAGIC, 7, int)
#define IODEV_IOCTL_UHF_CTL_PIN           _IOW(IODEV_IOC_MAGIC, 9, int)

extern "C"
JNIEXPORT void JNICALL
Java_com_gg_reader_api_utils_HksPower_uhf_1power(JNIEnv *env, jclass clazz, jint status) {
	int dev = open("/dev/iodev", O_RDWR);
	if (dev > 0) {
		ioctl(dev, IODEV_IOCTL_IDENTITY_UHF_POWER, &status);
		//ioctl(dev, IODEV_IOCTL_UHF_CTL_PIN,  &status);
		close(dev);
	}
}extern "C"
JNIEXPORT void JNICALL
Java_com_gg_reader_api_utils_HksPower_uhf_11io(JNIEnv *env, jclass clazz, jint val) {
	int dev = open("/dev/iodev", O_RDWR);
	if (dev > 0) {
		ioctl(dev, IODEV_IOCTL_UHF_CTL_PIN, &val);
		close(dev);
	}
}