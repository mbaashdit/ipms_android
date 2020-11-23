package com.aashdit.ipms.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aashdit.ipms.R;
import com.aashdit.ipms.network.Client;

import org.json.JSONArray;
import org.json.JSONException;

import moe.leer.tree2view.TreeView;
import moe.leer.tree2view.module.DefaultTreeNode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreeViewActivity extends AppCompatActivity {

    private static final String TAG = "TreeViewActivity";

    LinearLayout mLlContainer;
    TreeView treeView;

    private DefaultTreeNode<String> root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_view);

        mLlContainer = findViewById(R.id.ll_container);
        treeView = findViewById(R.id.tree_view);

        root = new DefaultTreeNode<String>("");
//        root.addChild(new DefaultTreeNode<String>("Child1"));
//        Log.d(TAG, "root's depth: " + root.getDepth());
//
//        DefaultTreeNode<String> child2 = new DefaultTreeNode<String>("Child2");
//        //Important: after create a node your should immediately add it.
//        root.addChild(child2);
//
//        DefaultTreeNode<String> childA = new DefaultTreeNode<String>("ChildA");
//        child2.addChild(childA);
//        child2.addChild(new DefaultTreeNode<String>("ChildB"));
//        child2.addChild(new DefaultTreeNode<String>("ChildC"));
//
//
//        Log.d(TAG, "childA's depth: " + childA.getDepth());
//        Log.d(TAG, "child2's depth: " + child2.getDepth());
//        root.addChild(new DefaultTreeNode<String>("Child3"));
//        root.addChild(new DefaultTreeNode<String>("Child4"));
//

        Client.getInstance().getApi().getData().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String res = response.body();
                    Log.i(TAG, "onResponse: " + res);
                    try {
                        JSONArray jsonArray = new JSONArray(res);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            DefaultTreeNode<String> par = new DefaultTreeNode<String>(jsonArray.optJSONObject(i).optString("name"));
                            root.addChild(par);

                            if (jsonArray.optJSONObject(i).optBoolean("hasChild")) {
                                JSONArray childArray = jsonArray.optJSONObject(i).optJSONArray("childs");
                                createChilsNode(par,childArray);
                            }
                        }
                        treeView.setRoot(root);
                        treeView.setRootVisible(true);
                        treeView.setDefaultAnimation(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

    private void createChilsNode(DefaultTreeNode<String> par, JSONArray childArray) {
        DefaultTreeNode<String> firstchild;
        if (childArray != null && childArray.length() > 0) {
            for (int j = 0; j < childArray.length(); j++) {

                firstchild = new DefaultTreeNode<String>(childArray.optJSONObject(j).optString("name"));
                par.addChild(firstchild);

                if (childArray.optJSONObject(j).optBoolean("hasChild")){
                    try {
                        JSONArray childChild = childArray.optJSONObject(j).getJSONArray("childs");
                        createChilsNode(firstchild,childChild);
//                        subChildNode(firstchild,childChild);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void subChildNode(DefaultTreeNode<String> firstchild, JSONArray childChild) {
        DefaultTreeNode<String> childhild;
        if (childChild != null && childChild.length() > 0){
            for (int i = 0; i < childChild.length(); i++) {
                childhild = new DefaultTreeNode<String>(childChild.optJSONObject(i).optString("name"));
                firstchild.addChild(childhild);
            }
        }
    }


}
