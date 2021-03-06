package es.source.code.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.source.code.App;
import es.source.code.R;
import es.source.code.adapter.GridFunctionAdapter;
import es.source.code.callback.OnItemBtnClickListener;
import es.source.code.callback.SimpleObserver;
import es.source.code.model.Event;
import es.source.code.model.Function;
import es.source.code.model.User;
import es.source.code.service.UpdateService;
import es.source.code.utils.Const;
import es.source.code.utils.RxBus;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Author        Daniel
 * Class:        MainScreen
 * Date:         2017/10/18 19:28
 * Description:  主导航界面
 */
public class MainScreen extends BaseActivity {

    @BindView(R.id.gv_function)
    GridView gvFunction;

    GridFunctionAdapter gridFunctionAdapter;
    List<Function> functionList;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ButterKnife.bind(this);
        initLayout();
        initData();
        initRxBus();
    }

    /**
     * author:      Daniel
     * description: 初始化数据
     */
    private void initData() {
        functionList = new ArrayList<>();

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                user = App.getInstance().getUser();
                int loginStatus = App.getInstance().getLoginStatus();

//                Intent intent = getIntent();
                // 根据intent判断是否显示点餐和订单
//                boolean show = Const.IntentValue.FROM.equals(intent.getStringExtra(Const.IntentKey.FROM)) || Const
//                        .IntentValue.LOGIN_SUCCESS.equals(intent.getStringExtra(Const.IntentKey.LOGIN_STATUS)) || Const
//                        .IntentValue.REGISTER_SUCCESS.equals(intent.getStringExtra(Const.IntentKey.LOGIN_STATUS));

//                if (loginStatus == 1) {
                    functionList.add(new Function(R.drawable.ic_order_white, R.drawable.guide_btn_order_selector,
                            getResources().getString(R.string.label_order), Const.Resources.FUNCTIONS_TAG.ORDER));
                    functionList.add(new Function(R.drawable.ic_form_white, R.drawable.guide_btn_form_selector,
                            getResources().getString(R.string.label_form), Const.Resources.FUNCTIONS_TAG.FORM));
//                }

                functionList.add(new Function(R.drawable.ic_account_white, R.drawable
                        .guide_btn_account_selector, getResources().getString(R
                        .string.label_account), Const.Resources.FUNCTIONS_TAG.ACCOUNT));
                functionList.add(new Function(R.drawable.ic_help_white, R.drawable
                        .guide_btn_help_selector, getResources().getString(R
                        .string.label_help), Const.Resources.FUNCTIONS_TAG.HELP));

                e.onNext(Const.EventKey.REFRESH_FUNCTONS);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onEvent(String string) {
                        RxBus.getInstance().post(new Event<>(string));
                    }
                });
    }

    /**
     * author:      Daniel
     * description: 初始化控件
     */
    private void initLayout() {
        gridFunctionAdapter = new GridFunctionAdapter(null, mContext);
        gridFunctionAdapter.setOnItemBtnClickListener(new OnItemBtnClickListener<Function>() {
            @Override
            public void onClick(Function function, int position) {
                onFunctionCLick(function.getTag());
            }
        });
        gvFunction.setAdapter(gridFunctionAdapter);
    }

    /**
     * author:      Daniel
     * description: 刷新控件
     */
    private void refreshLayout() {
        gridFunctionAdapter.setData(functionList);
        gridFunctionAdapter.notifyDataSetChanged();
    }

    /**
     * author:      Daniel
     * description: 初始化bus
     */
    private void initRxBus() {
        RxBus.getInstance().register().subscribe(new Consumer<Event>() {
            @Override
            public void accept(Event event) throws Exception {
                if (Const.EventKey.REFRESH_FUNCTONS.equals(event.getEventKey())) {
                    refreshLayout();
                }
            }
        });
    }

    public void onFunctionCLick(Const.Resources.FUNCTIONS_TAG functionTag) {
        // TODO: 2017/10/10 跳转页面
        Intent intent;
        switch (functionTag) {
            case ORDER:
                intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Const.BundleKey.USER, user);
                intent.setClass(mContext, FoodView.class).putExtras(bundle);
                startActivity(intent);
                break;
            case FORM:
                Intent serviceIntent = new Intent(mContext, UpdateService.class);
                mContext.startService(serviceIntent);
                break;
            case ACCOUNT:
                intent = new Intent(mContext, LoginOrRegister.class);
                startActivityForResult(intent, Const.ActivityCode.MAIN_SCREEN);
                break;
            case HELP:
                showActivity(SCOSHelper.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (resultCode) {
            case Const.ActivityCode.LOGIN_OR_REGISTER:
                // 登录或注册成功处理。
                if (Const.IntentValue.REGISTER_SUCCESS.equals(intent.getStringExtra(Const
                        .IntentKey.LOGIN_STATUS))) {
                    showToast(getString(R.string.toast_new_account));
                }
                initData();
        }
    }
}
