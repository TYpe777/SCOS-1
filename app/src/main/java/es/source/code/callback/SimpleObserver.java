package es.source.code.callback;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Author        Daniel
 * Class:        SimpleObserver
 * Date:         2017/10/18 19:42
 * Description:  简单封装observer接口
 */
public abstract class SimpleObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {
        onEvent(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    public abstract void onEvent(T t);
}
