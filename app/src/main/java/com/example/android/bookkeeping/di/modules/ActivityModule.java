package com.example.android.bookkeeping.di.modules;

        import android.content.Context;
        import android.support.v7.app.AppCompatActivity;
        import android.widget.ArrayAdapter;

        import com.example.android.bookkeeping.R;
        import com.example.android.bookkeeping.data.model.AccountSaver;
        import com.example.android.bookkeeping.ui.account.AccountsMvpPresenter;
        import com.example.android.bookkeeping.ui.account.AccountsMvpView;
        import com.example.android.bookkeeping.ui.account.AccountsPresenter;
        import com.example.android.bookkeeping.ui.account.create.CreateAccountMvpPresenter;
        import com.example.android.bookkeeping.ui.account.create.CreateAccountMvpView;
        import com.example.android.bookkeeping.ui.account.create.CreateAccountPresenter;
        import com.example.android.bookkeeping.ui.adapters.AccountsListAdapter;
        import com.example.android.bookkeeping.ui.chart.ChartMvpPresenter;
        import com.example.android.bookkeeping.ui.chart.ChartMvpView;
        import com.example.android.bookkeeping.ui.chart.ChartPresenter;
        import com.example.android.bookkeeping.ui.cloud.auth.FirebaseAuthMvpPresenter;
        import com.example.android.bookkeeping.ui.cloud.auth.FirebaseAuthMvpView;
        import com.example.android.bookkeeping.ui.cloud.auth.FirebaseAuthPresenter;
        import com.example.android.bookkeeping.ui.cloud.storage.FirebaseStorageMvpPresenter;
        import com.example.android.bookkeeping.ui.cloud.storage.FirebaseStorageMvpView;
        import com.example.android.bookkeeping.ui.cloud.storage.FirebaseStoragePresenter;
        import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialog;
        import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialogMvpPresenter;
        import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialogMvpView;
        import com.example.android.bookkeeping.ui.dialogs.currencies.CurrenciesDialogPresenter;
        import com.example.android.bookkeeping.ui.dialogs.history.CurrenciesHistoryMvpPresenter;
        import com.example.android.bookkeeping.ui.dialogs.history.CurrenciesHistoryMvpView;
        import com.example.android.bookkeeping.ui.dialogs.history.CurrenciesHistoryPresenter;
        import com.example.android.bookkeeping.ui.transaction.TransactionMvpPresenter;
        import com.example.android.bookkeeping.ui.transaction.TransactionMvpView;
        import com.example.android.bookkeeping.ui.transaction.TransactionPresenter;
        import com.example.android.bookkeeping.ui.transaction.create.CreateTransactionMvpPresenter;
        import com.example.android.bookkeeping.ui.transaction.create.CreateTransactionMvpView;
        import com.example.android.bookkeeping.ui.transaction.create.CreateTransactionPresenter;

        import java.util.ArrayList;

        import javax.inject.Inject;
        import javax.inject.Singleton;

        import dagger.Module;
        import dagger.Provides;
        import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {

    private final Context mActivity;

    public ActivityModule(Context activity) {
        mActivity = activity;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return mActivity;
    }

    @Singleton
    @Provides
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Singleton
    @Provides
    AccountsMvpPresenter<AccountsMvpView> provideAccountsMvpPresenter(
            AccountsPresenter<AccountsMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    CreateAccountMvpPresenter<CreateAccountMvpView> provideCreateAccountMvpView(CreateAccountPresenter<CreateAccountMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    CurrenciesDialogMvpPresenter<CurrenciesDialogMvpView> provideCurrenciesDialogMvpPresenter(CurrenciesDialogPresenter<CurrenciesDialogMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    TransactionMvpPresenter<TransactionMvpView> provideTransactionMvpPresenter(TransactionPresenter<TransactionMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    CreateTransactionMvpPresenter<CreateTransactionMvpView> provideCreateTransactionMvpPresenter(CreateTransactionPresenter<CreateTransactionMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    ChartMvpPresenter<ChartMvpView> provideChartMvpPresenter(ChartPresenter<ChartMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    CurrenciesHistoryMvpPresenter<CurrenciesHistoryMvpView> provideCurrenciesHistoryPresenter(CurrenciesHistoryPresenter<CurrenciesHistoryMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    FirebaseAuthMvpPresenter<FirebaseAuthMvpView> provideFirebaseAuthPresenter(FirebaseAuthPresenter<FirebaseAuthMvpView> presenter) {
        return presenter;
    }

    @Singleton
    @Provides
    FirebaseStorageMvpPresenter<FirebaseStorageMvpView> provideFirebaseStoragePresenter(FirebaseStoragePresenter<FirebaseStorageMvpView> presenter) {
        return presenter;
    }
}
