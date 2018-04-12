// IDeleteFiles.aidl
package first.test.com.bscenter.core.engine.service.delete;

// Declare any non-default types here with import statements

import first.test.com.bscenter.core.engine.service.delete.IDeleteFilesCallback;

interface IDeleteFiles {
	void start(in List<String> files);
	void cancel();
	void pause();
	void resume();
	void registerCallback(IDeleteFilesCallback callback);
	void unregisterCallback(IDeleteFilesCallback callback);
}