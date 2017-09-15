package com.sandbox.parker.sandboxapi.model;

/**
 * Created by parker on 9/15/17.
 */

public class ExampleModel {

    // The Presenter class tied to this Model.
    // Every Model should have only ONE Presenter.
    // But Presenters can access several Models at the same time.
    private ExampleModel.Presenter mPresenter;

    // The constructor of the Model.
    // NOTE: The only way to create an instance of this specific model
    //       is to pass the Model an Object the implements ExampleModel.Presenter.
    //       This ensures that the Model can call it's callback methods.
    public ExampleModel(ExampleModel.Presenter presenter) {
        mPresenter = presenter;
    }

    // The methods that the Presenter needs to implement.
    interface Presenter {

        void sayHello();

    }

}
