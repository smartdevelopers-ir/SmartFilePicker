package ir.smartdevelopers.smartfilebrowser.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ir.smartdevelopers.smartfilebrowser.customClasses.OnResultListener;

public class CallbackViewModel extends ViewModel {

    private MutableLiveData<OnResultListener> mOnResultListenerLiveData;

    public CallbackViewModel() {
        mOnResultListenerLiveData = new MutableLiveData<>();
    }
    public void registerResultListener(OnResultListener resultListener){
        mOnResultListenerLiveData.setValue(resultListener);
    }

}
