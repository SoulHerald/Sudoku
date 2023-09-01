package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {


    SudokuGenerator sudokuGenerator = new SudokuGenerator();

    //TextView二维数组
    TextView[][] gameMap = new TextView[9][9];

    //二维用户输入数独数组
    int[][] userInputGameMap = new int[9][9];

    //二维用户输入数独数组
    final int[][] originalGameMap = sudokuGenerator.getBoard();

    //一维可选数字数组
    ArrayList<TextView> optionalNumList = new ArrayList<>();

    //选择的位置数组
    int[] selectPosition = new int[2];


    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (this.getResources().getConfiguration().uiMode == 0x21) {
            ImmersionBar.with(this)
                    .transparentStatusBar()  //透明状态栏，不写默认透明色
                    .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                    .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                    .init();
        } else if (this.getResources().getConfiguration().uiMode == 0x11) {
            ImmersionBar.with(this)
                    .transparentStatusBar()  //透明状态栏，不写默认透明色
                    .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                    .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
                    .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                    .init();
        }

        for (int i = 0; i < 9; i++) {
            System.arraycopy(originalGameMap[i], 0, userInputGameMap[i], 0, 9);
        }

        initGameMap();
    }


    //刷新地图区域
    public void RefreshMapSection() {
        GridLayout gridLayout = findViewById(R.id.Game_Map);

        gridLayout.removeAllViews();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                //生成并设置linearLayout长宽
                LinearLayout linearLayout = new LinearLayout(this);

                linearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


                linearLayout.setGravity(Gravity.CENTER);


                //设置并设置TextView属性
                TextView textView = new TextView(this);
                if (i == selectPosition[0] && j == selectPosition[1]) {
                    textView.setBackgroundResource(R.drawable.cell_select_background);
                } else {
                    textView.setBackgroundResource(R.drawable.cell_clear_background);
                }
                textView.setGravity(Gravity.CENTER);

                //设置文字
                if (userInputGameMap[i][j] == 0) {
                    textView.setText("");
                } else {
                    textView.setText(String.valueOf(userInputGameMap[i][j]));
                }

                //设置文字颜色
                if (originalGameMap[i][j] != 0) {
                    textView.setTextColor(Color.parseColor("#000000"));
                } else {
                    textView.setTextColor(Color.parseColor("#eb7171"));
                }

                linearLayout.addView(textView);


                LinearLayout.LayoutParams lp;
                lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
                lp.width = cellWidth();
                lp.height = cellWidth();
                linearLayout.setPadding(2, 2, 2, 2);

                linearLayout.setLayoutParams(lp);

                gridLayout.addView(linearLayout);
                gameMap[i][j] = textView;

            }
        }


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextView textView = gameMap[i][j];
                int finalI = i;
                int finalJ = j;
                textView.setOnClickListener(view -> {
                    cellClearColor();
                    textView.setBackgroundResource(R.drawable.cell_select_background);
                    selectPosition[0] = finalI;
                    selectPosition[1] = finalJ;
                    RefreshInputSection();
                });
            }
        }

    }


    //刷新输入区域
    public void RefreshInputSection() {

        GridLayout input_Section = findViewById(R.id.Input_Section);

        input_Section.removeAllViews();

        LinkedHashSet<String> optionalNum = initInputSection();

        ArrayList<String> tempList = new ArrayList<>(optionalNum);

        if (!optionalNum.isEmpty()) {
            for (int i = 0; i < optionalNum.size(); i++) {
                LinearLayout linearLayout = new LinearLayout(this);

                linearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


                linearLayout.setGravity(Gravity.CENTER);


                //设置并设置TextView属性
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.drawable.optional_num_background);
                textView.setGravity(Gravity.CENTER);

                //设置文字
                textView.setText(tempList.get(i));

                linearLayout.addView(textView);


                LinearLayout.LayoutParams lp;
                lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
                lp.width = cellWidth();
                lp.height = cellWidth();
                linearLayout.setPadding(2, 2, 2, 2);

                linearLayout.setLayoutParams(lp);


                input_Section.addView(linearLayout);

                optionalNumList.add(textView);
            }
        }


        for (int i = 0; i < optionalNumList.size(); i++) {
            int finalI = i;
            optionalNumList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (optionalNumList.get(finalI).getText().toString().equals("删除")) {
                        deleteNumber(selectPosition[0], selectPosition[1]);
                    } else {
                        addNumber(selectPosition[0], selectPosition[1], optionalNumList.get(finalI).getText().toString());
                    }

                }
            });


        }


    }

    //添加数字
    public void addNumber(int x, int y, String number) {
        userInputGameMap[x][y] = Integer.parseInt(number);
        RefreshMapSection();
        RefreshInputSection();
    }

    //添加数字
    public void deleteNumber(int x, int y) {
        userInputGameMap[x][y] = 0;
        RefreshMapSection();
        RefreshInputSection();
    }

    //返回可选数字
    public LinkedHashSet<String> initInputSection() {

        //查询横竖行和其所在的九宫格的所有数字
        HashSet<String> haveNum = new HashSet<>();

        LinkedHashSet<String> optionalNum = new LinkedHashSet<>();


        String[] num = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        if (originalGameMap[selectPosition[0]][selectPosition[1]] == 0) {
            if (userInputGameMap[selectPosition[0]][selectPosition[1]] == 0) {
                //横列
                for (int i = 0; i < 9; i++) {
                    if (userInputGameMap[i][selectPosition[1]] != 0) {
                        haveNum.add(String.valueOf(userInputGameMap[i][selectPosition[1]]));
                    }
                }

                //竖
                for (int i = 0; i < 9; i++) {
                    if (userInputGameMap[selectPosition[0]][i] != 0) {
                        haveNum.add(String.valueOf(userInputGameMap[selectPosition[0]][i]));
                    }
                }


                //九宫格
                int heng_start = selectPosition[0] / 3 * 3;
                int shu_start = selectPosition[1] / 3 * 3;
                for (int i = heng_start; i < heng_start + 2; i++) {
                    for (int j = shu_start; j < shu_start + 2; j++) {
                        if (userInputGameMap[i][j] != 0) {
                            haveNum.add(String.valueOf(userInputGameMap[i][j]));
                        }
                    }
                }


                ArrayList<String> tempList = new ArrayList<>(haveNum);
                //计算可以填充的数字
                for (int i = 0; i < num.length; i++) {
                    if (!tempList.contains(num[i])) {
                        optionalNum.add(num[i]);
                    }
                }
            } else {
                optionalNum.add("删除");
            }
        }

        return optionalNum;
    }


    //初始化游戏地图
    public void initGameMap() {


        GridLayout gridLayout = findViewById(R.id.Game_Map);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                //生成并设置linearLayout长宽
                LinearLayout linearLayout = new LinearLayout(this);

                linearLayout.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


                linearLayout.setGravity(Gravity.CENTER);


                //设置并设置TextView属性
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.drawable.cell_clear_background);
                textView.setGravity(Gravity.CENTER);

                //设置文字
                if (userInputGameMap[i][j] == 0) {
                    textView.setText("");
                } else {
                    textView.setText(String.valueOf(userInputGameMap[i][j]));
                }

                //设置文字颜色
                if (originalGameMap[i][j] != 0) {
                    textView.setTextColor(Color.parseColor("#000000"));
                } else {
                    textView.setTextColor(Color.parseColor("#eb7171"));
                }


                linearLayout.addView(textView);


                LinearLayout.LayoutParams lp;
                lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
                lp.width = cellWidth();
                lp.height = cellWidth();
                linearLayout.setPadding(2, 2, 2, 2);

                linearLayout.setLayoutParams(lp);


                gridLayout.addView(linearLayout);

                gameMap[i][j] = textView;

            }
        }


        cellClearColor();

        gameMap[4][4].setBackgroundResource(R.drawable.cell_select_background);

        selectPosition[0] = 4;
        selectPosition[1] = 4;


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextView textView = gameMap[i][j];
                int finalI = i;
                int finalJ = j;
                textView.setOnClickListener(view -> {
                    cellClearColor();
                    textView.setBackgroundResource(R.drawable.cell_select_background);
                    selectPosition[0] = finalI;
                    selectPosition[1] = finalJ;
                    RefreshInputSection();
                });
            }
        }

        RefreshInputSection();
    }


    //恢复所有格子默认颜色
    public void cellClearColor() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                gameMap[i][j].setBackgroundResource(R.drawable.cell_clear_background);
            }
        }
    }

    //通过获取屏幕宽和高，计算得到格子的长度
    public int cellWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels - dip2px(this, 20);
        int screenHeight = dm.heightPixels - dip2px(this, 20);
        return Math.min(screenWidth, screenHeight) / 9;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}