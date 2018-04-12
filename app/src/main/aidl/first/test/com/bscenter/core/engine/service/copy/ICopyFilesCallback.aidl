// ICopyFilesCallback.aidl
package first.test.com.bscenter.core.engine.service.copy;

// Declare any non-default types here with import statements

interface ICopyFilesCallback {

	void onStart();
	void onPause();
	void postUpdate(String fileName, long allSize, long hasDelete, int progress);
	void onCancel(long hasDeletedSize);
	void onFinish(long hasDeletedSize);
	void onResume();
}