package dagger.extension.example.service;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.AllowStubGeneration;
import dagger.Replaceable;
import dagger.extension.example.di.scope.ActivityScope;
import io.reactivex.subjects.PublishSubject;

@ActivityScope
public class PermissionService {

    private final AppCompatActivity activity;
    private final PublishSubject<PermissionResult> container = PublishSubject.create();

    @Inject @Replaceable
    public PermissionService(AppCompatActivity activity) {
        this.activity = activity;
    }

    public boolean isPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    public PublishSubject<PermissionResult> onPermissionGranted() {
        return this.container;
    }

    public void dispatchEvent(PermissionResult permissionEvent) {
        this.container.onNext(permissionEvent);
    }

}
