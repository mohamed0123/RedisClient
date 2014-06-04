package com.cxy.redisclient.presentation;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.cxy.redisclient.domain.DataNode;
import com.cxy.redisclient.domain.Favorite;
import com.cxy.redisclient.domain.Node;
import com.cxy.redisclient.domain.NodeType;
import com.cxy.redisclient.domain.Server;
import com.cxy.redisclient.dto.ContainerInfo;
import com.cxy.redisclient.dto.HashInfo;
import com.cxy.redisclient.dto.ListInfo;
import com.cxy.redisclient.dto.RenameInfo;
import com.cxy.redisclient.dto.SetInfo;
import com.cxy.redisclient.dto.StringInfo;
import com.cxy.redisclient.dto.ZSetInfo;
import com.cxy.redisclient.presentation.favorite.AddFavoriteDialog;
import com.cxy.redisclient.presentation.favorite.OrganizeFavoriteDialog;
import com.cxy.redisclient.presentation.hash.NewHashDialog;
import com.cxy.redisclient.presentation.key.RenameKeysDialog;
import com.cxy.redisclient.presentation.list.NewListDialog;
import com.cxy.redisclient.presentation.server.AddServerDialog;
import com.cxy.redisclient.presentation.server.UpdateServerDialog;
import com.cxy.redisclient.presentation.set.NewSetDialog;
import com.cxy.redisclient.presentation.string.NewStringDialog;
import com.cxy.redisclient.presentation.zset.NewZSetDialog;
import com.cxy.redisclient.service.FavoriteService;
import com.cxy.redisclient.service.HashService;
import com.cxy.redisclient.service.ListService;
import com.cxy.redisclient.service.NodeService;
import com.cxy.redisclient.service.ServerService;
import com.cxy.redisclient.service.SetService;
import com.cxy.redisclient.service.ZSetService;

public class RedisClient {
	private static Shell shlRedisClient;

	private static final String DB_PREFIX = "db";
	private static final String NODE_TYPE = "type";
	private static final String NODE_ID = "id";
	private static final String ITEM_OPENED = "open";
	private static final String FAVORITE = "favorite";

	private Tree tree;
	private Table table;
	private Text text;

	private Menu menu_server;
	private Menu menu;
	private Menu menu_null;
	private Menu menu_DB;
	private Menu menu_favorite;

	private MenuItem mntmRename_1;
	private MenuItem mntmDelete_2;
	private MenuItem mntmRename_2;
	private MenuItem mntmDelete_3;
	private MenuItem mntmAdd_Favorite;

	private ServerService service1 = new ServerService();
	private NodeService service2 = new NodeService();
	private FavoriteService service3 = new FavoriteService();
	private ListService service4 = new ListService();
	private SetService service5 = new SetService();
	private ZSetService service6 = new ZSetService();
	private HashService service7 = new HashService();


	private TreeItem rootRedisServers;

	private Image redisImage;
	private Image dbImage;
	private Image containerImage;
	private Image strImage;
	private Image setImage;
	private Image listImage;
	private Image zsetImage;
	private Image hashImage;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RedisClient window = new RedisClient();
		window.open();
	}

	/**
	 * Open the window.
	 * 
	 */
	public void open() {
		Display display = null;

		display = Display.getDefault();
		createContents();

		shlRedisClient.open();
		shlRedisClient.layout();

		while (!shlRedisClient.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Exception e) {
				MessageDialog.openError(shlRedisClient, "Error",
						e.getMessage());
			}
		}
		display.dispose();
	}

	/**
	 * Create contents of the window.
	 * 
	 */
	protected void createContents() {
		initShell();

		initMenu();

		initSash();

	}

	private void initImage() {
		redisImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/redis.png")); 
		dbImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/db.png")); 
		strImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/string.png")); 
		setImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/set.png")); 
		listImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/list.png")); 
		zsetImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/zset.png")); 
		hashImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/hash.png")); 
		containerImage = new Image(tree.getDisplay(),getClass().getResourceAsStream("/container.png")); 
	}
	private void initShell() {
		shlRedisClient = new Shell();
		shlRedisClient.setSize(750, 529);
		shlRedisClient.setText("Redis client");
		shlRedisClient.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	private void initSash() {
		SashForm sashForm_2 = new SashForm(shlRedisClient, SWT.SMOOTH
				| SWT.VERTICAL);
		sashForm_2.setSashWidth(0);

		text = new Text(sashForm_2, SWT.BORDER | SWT.SEARCH);
		text.setEditable(false);
		SashForm sashForm = new SashForm(sashForm_2, SWT.NONE);

		tree = new Tree(sashForm, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		initImage();
		
		rootRedisServers = new TreeItem(tree, SWT.NONE);
		rootRedisServers.setImage(redisImage);
		rootRedisServers.setText("Redis Servers");
		rootRedisServers.setData(NODE_TYPE, NodeType.ROOT);
		rootRedisServers.setExpanded(true);
		rootRedisServers.setData(ITEM_OPENED, true);

		initMenuDB();

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				treeItemSelected(false);
			}
		});

		SashForm sashForm_1 = new SashForm(sashForm, SWT.VERTICAL);

		initTable(sashForm_1);

		sashForm_1.setWeights(new int[] { 1 });

		sashForm.setWeights(new int[] { 1, 3 });
		sashForm_2.setWeights(new int[] { 22, 368 });

		initMenuNull();

		initMenuServer();

		initServers();
	}

	private void parseContainer(TreeItem item, ContainerInfo info) {
		TreeItem parent = item.getParentItem();
		if (item.getData(NODE_TYPE) == NodeType.CONTAINER) {
			String container = item.getText();
			if (info.getContainer() != null)
				info.setContainer(container + ":" + info.getContainer());
			else
				info.setContainer(container + ":");

			parseContainer(parent, info);
		} else if (item.getData(NODE_TYPE) == NodeType.DATABASE) {
			int db = (Integer) item.getData(NODE_ID);
			info.setDb(db);
			parseContainer(parent, info);
		} else if (item.getData(NODE_TYPE) == NodeType.SERVER) {
			int id = (Integer) item.getData(NODE_ID);
			info.setId(id);
			info.setServerName(item.getText());
			return;
		}
	}

	private void initMenuDB() {
		menu_DB = new Menu(shlRedisClient);

		MenuItem mntmNew_1 = new MenuItem(menu_DB, SWT.CASCADE);
		mntmNew_1.setText("new");

		Menu menu_1 = new Menu(mntmNew_1);
		mntmNew_1.setMenu(menu_1);

		MenuItem menuItem_1 = new MenuItem(menu_1, SWT.NONE);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newString();
			}
		});
		menuItem_1.setText("String");

		MenuItem menuItem_2 = new MenuItem(menu_1, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newList();
			}
		});
		menuItem_2.setText("List");

		MenuItem menuItem_3 = new MenuItem(menu_1, SWT.NONE);
		menuItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newSet();
			}
		});
		menuItem_3.setText("Set");

		MenuItem mntmSortedSet = new MenuItem(menu_1, SWT.NONE);
		mntmSortedSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newZSet();
			}
		});
		mntmSortedSet.setText("Sorted Set");

		MenuItem mntmHash_1 = new MenuItem(menu_1, SWT.NONE);
		mntmHash_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newHash();
			}
		});
		mntmHash_1.setText("Hash");

		mntmRename_1 = new MenuItem(menu_DB, SWT.NONE);
		mntmRename_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				renameContainer();
			}
		});
		mntmRename_1.setText("rename");

		mntmDelete_2 = new MenuItem(menu_DB, SWT.NONE);
		mntmDelete_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] items = tree.getSelection();
				deleteCotainer(items[0]);
			}
		});
		mntmDelete_2.setText("delete");

		new MenuItem(menu_DB, SWT.SEPARATOR);

		MenuItem mntmAddToFavorites = new MenuItem(menu_DB, SWT.NONE);
		mntmAddToFavorites.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addFavorite();
			}
		});
		mntmAddToFavorites.setText("add to favorites");

		new MenuItem(menu_DB, SWT.SEPARATOR);

		MenuItem mntmCut = new MenuItem(menu_DB, SWT.NONE);
		mntmCut.setText("cut");

		MenuItem mntmCopy_1 = new MenuItem(menu_DB, SWT.NONE);
		mntmCopy_1.setText("copy");

		MenuItem mntmPaste_1 = new MenuItem(menu_DB, SWT.NONE);
		mntmPaste_1.setEnabled(false);
		mntmPaste_1.setText("paste");
		
		new MenuItem(menu_DB, SWT.SEPARATOR);
		
		MenuItem mntmRefresh_2 = new MenuItem(menu_DB, SWT.NONE);
		mntmRefresh_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] items = tree.getSelection();
				dbContainerItemSelected(items[0], true);
			}
		});
		mntmRefresh_2.setText("refresh");
	}

	private void initTable(SashForm sashForm_1) {
		table = new Table(sashForm_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] tableItems = table.getSelection();
				TreeItem[] treeItems = tree.getSelection();

				if (treeItems.length > 0) {
					for (TreeItem treeItem : treeItems[0].getItems()) {
						String treeText = treeItem.getText();
						String tableText = tableItems[0].getText(0);
						String type = tableItems[0].getText(1);
						if (treeText.equals(tableText)) {
							if (type.equals(NodeType.DATABASE.toString())
									|| type.equals(NodeType.CONTAINER
											.toString()))
								dbContainerItemSelected(treeItem, false);
							else if (type.equals(NodeType.SERVER.toString()))
								serverItemSelected(treeItem, false);
							break;
						}

					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}
		});
		table.setHeaderVisible(true);

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("name");

		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("type");

		TableColumn tblclmnSize = new TableColumn(table, SWT.NONE);
		tblclmnSize.setWidth(100);
		tblclmnSize.setText("size");
	}

	private void initMenuServer() {
		menu_server = new Menu(shlRedisClient);

		MenuItem mntmRename = new MenuItem(menu_server, SWT.NONE);
		mntmRename.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateServer();
			}
		});
		mntmRename.setText("update");

		final MenuItem mntmDelete = new MenuItem(menu_server, SWT.NONE);
		mntmDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeServer();
			}
		});
		mntmDelete.setText("remove");
		
		new MenuItem(menu_server, SWT.SEPARATOR);
		
		MenuItem mntmRefresh_3 = new MenuItem(menu_server, SWT.NONE);
		mntmRefresh_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] items = tree.getSelection();
				serverItemSelected(items[0], true);
			}
		});
		mntmRefresh_3.setText("refresh");
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				Point point = new Point(arg0.x, arg0.y);
				TreeItem selectedItem = tree.getItem(point);
				if (arg0.button == 3) {
					if (selectedItem == null)
						tree.setMenu(menu_null);
					else {
						NodeType type = (NodeType) selectedItem
								.getData(NODE_TYPE);

						if(type == NodeType.ROOT)
							tree.setMenu(menu_null);
						else if (type == NodeType.SERVER)
							tree.setMenu(menu_server);
						else if (type == NodeType.DATABASE
								|| type == NodeType.CONTAINER) {
							if (type == NodeType.DATABASE) {
								mntmRename_1.setEnabled(false);
								mntmDelete_2.setEnabled(false);
							} else {
								mntmRename_1.setEnabled(true);
								mntmDelete_2.setEnabled(true);
							}
							tree.setMenu(menu_DB);
						}

					}
				}
			}
		});
	}

	private void initMenuNull() {
		menu_null = new Menu(shlRedisClient);

		MenuItem mntmNewConnection = new MenuItem(menu_null, SWT.NONE);
		mntmNewConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addServer();
			}
		});
		mntmNewConnection.setText("add server");

		MenuItem mntmRefresh = new MenuItem(menu_null, SWT.NONE);
		mntmRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				rootItemSelected(true);
			}
		});
		mntmRefresh.setText("refresh");
	}

	private void initMenu() {
		menu = new Menu(shlRedisClient, SWT.BAR);
		shlRedisClient.setMenuBar(menu);

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("&Server");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		MenuItem mntmNew = new MenuItem(menu_1, SWT.NONE);
		mntmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				addServer();
			}
		});
		mntmNew.setText("Add");

		MenuItem mntmEdit = new MenuItem(menu_1, SWT.NONE);
		mntmEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateServer();
			}
		});
		mntmEdit.setText("Update");

		MenuItem mntmDelete_1 = new MenuItem(menu_1, SWT.NONE);
		mntmDelete_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				removeServer();
			}
		});
		mntmDelete_1.setText("Remove");

		new MenuItem(menu_1, SWT.SEPARATOR);

		MenuItem mntmProperties = new MenuItem(menu_1, SWT.NONE);
		mntmProperties.setText("Properties");

		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shlRedisClient.close();
			}
		});
		mntmExit.setText("Exit");

		MenuItem mntmData = new MenuItem(menu, SWT.CASCADE);
		mntmData.setText("Data");

		Menu menu_4 = new Menu(mntmData);
		mntmData.setMenu(menu_4);

		MenuItem mntmAdd = new MenuItem(menu_4, SWT.CASCADE);
		mntmAdd.setText("New");

		Menu menu_5 = new Menu(mntmAdd);
		mntmAdd.setMenu(menu_5);

		MenuItem mntmString = new MenuItem(menu_5, SWT.NONE);
		mntmString.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newString();
			}
		});
		mntmString.setText("String");

		MenuItem mntmList = new MenuItem(menu_5, SWT.NONE);
		mntmList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newList();
			}
		});
		mntmList.setText("List");

		MenuItem mntmSet = new MenuItem(menu_5, SWT.NONE);
		mntmSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newSet();
			}
		});
		mntmSet.setText("Set");

		MenuItem mntmSortset = new MenuItem(menu_5, SWT.NONE);
		mntmSortset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newZSet();
			}
		});
		mntmSortset.setText("Sorted Set");

		MenuItem mntmHash = new MenuItem(menu_5, SWT.NONE);
		mntmHash.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newHash();
			}
		});
		mntmHash.setText("Hash");

		mntmRename_2 = new MenuItem(menu_4, SWT.NONE);
		mntmRename_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				renameContainer();
			}
		});
		mntmRename_2.setText("Rename");

		mntmDelete_3 = new MenuItem(menu_4, SWT.NONE);
		mntmDelete_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] items = tree.getSelection();
				NodeType type = (NodeType) items[0].getData(NODE_TYPE);
				if (type == NodeType.CONTAINER)
					deleteCotainer(items[0]);
				else
					MessageDialog.openError(shlRedisClient, "error",
							"please select a container to delete");
			}
		});
		mntmDelete_3.setText("Delete");

		new MenuItem(menu_4, SWT.SEPARATOR);

		MenuItem mntmcut = new MenuItem(menu_4, SWT.NONE);
		mntmcut.setText("&Cut");

		MenuItem mntmCopy = new MenuItem(menu_4, SWT.NONE);
		mntmCopy.setText("C&opy");

		MenuItem mntmPaste = new MenuItem(menu_4, SWT.NONE);
		mntmPaste.setText("&Paste");

		new MenuItem(menu_4, SWT.SEPARATOR);

		MenuItem mntmFind = new MenuItem(menu_4, SWT.NONE);
		mntmFind.setText("Find");

		MenuItem mntmReplace = new MenuItem(menu_4, SWT.NONE);
		mntmReplace.setText("Find next");

		new MenuItem(menu_4, SWT.SEPARATOR);

		MenuItem mntmImport = new MenuItem(menu_4, SWT.NONE);
		mntmImport.setText("Import");

		MenuItem mntmExport = new MenuItem(menu_4, SWT.NONE);
		mntmExport.setText("Export");

		new MenuItem(menu_4, SWT.SEPARATOR);

		MenuItem mntmRefresh_1 = new MenuItem(menu_4, SWT.NONE);
		mntmRefresh_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				treeItemSelected(true);
			}
		});
		mntmRefresh_1.setText("Refresh");

		MenuItem mntmFavorites = new MenuItem(menu, SWT.CASCADE);
		mntmFavorites.setText("Favorites");

		menu_favorite = new Menu(mntmFavorites);
		mntmFavorites.setMenu(menu_favorite);

		mntmAdd_Favorite = new MenuItem(menu_favorite, SWT.NONE);
		mntmAdd_Favorite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addFavorite();
			}
		});
		mntmAdd_Favorite.setText("Add");

		MenuItem mntmOrganize = new MenuItem(menu_favorite, SWT.NONE);
		mntmOrganize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				OrganizeFavoriteDialog dialog = new OrganizeFavoriteDialog(
						shlRedisClient, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

				List<Favorite> favorites = (List<Favorite>) dialog.open();
				if (favorites != null) {
					service3.updateList(favorites);

					removeFavoriteMenuItem();
					addFavoriteMenuItem();
				}

			}
		});
		mntmOrganize.setText("Organize");

		addFavoriteMenuItem();

		MenuItem mntmTool = new MenuItem(menu, SWT.CASCADE);
		mntmTool.setText("Tool");

		Menu menu_6 = new Menu(mntmTool);
		mntmTool.setMenu(menu_6);

		MenuItem mntmSet_2 = new MenuItem(menu_6, SWT.CASCADE);
		mntmSet_2.setText("Set");

		Menu menu_3 = new Menu(mntmSet_2);
		mntmSet_2.setMenu(menu_3);

		MenuItem mntmDiff = new MenuItem(menu_3, SWT.NONE);
		mntmDiff.setText("Difference");

		MenuItem mntmInter = new MenuItem(menu_3, SWT.NONE);
		mntmInter.setText("Intersection");

		MenuItem mntmUnion = new MenuItem(menu_3, SWT.NONE);
		mntmUnion.setText("Union");

		new MenuItem(menu_6, SWT.SEPARATOR);

		MenuItem mntmPubsub = new MenuItem(menu_6, SWT.NONE);
		mntmPubsub.setText("Pub/Sub");

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu menu_2 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_2);

		MenuItem mntmReportBug = new MenuItem(menu_2, SWT.NONE);
		mntmReportBug.setText("Report bug");

		MenuItem mntmDonation = new MenuItem(menu_2, SWT.NONE);
		mntmDonation.setText("Donation");

		new MenuItem(menu_2, SWT.SEPARATOR);

		MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
		mntmAbout.setText("About");
	}

	private void removeFavoriteMenuItem() {
		int num = menu_favorite.getItemCount();
		if (num > 2) {
			MenuItem[] items = menu_favorite.getItems();
			for (int i = 2; i < num; i++) {
				items[i].dispose();
			}
		}

	}

	private void addFavoriteMenuItem() {
		List<Favorite> favorites = service3.listAll();
		if (favorites.size() > 0) {
			new MenuItem(menu_favorite, SWT.SEPARATOR);
			for (Favorite favorite : favorites) {
				final MenuItem menuItem = new MenuItem(menu_favorite, SWT.NONE);
				menuItem.setText(favorite.getName());
				menuItem.setData(FAVORITE, favorite);
				menuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Favorite favorite = (Favorite) menuItem
								.getData(FAVORITE);
						int sid = favorite.getServerID();

						rootItemSelected(false);
						TreeItem[] treeItems = rootRedisServers.getItems();
						for (TreeItem treeItem : treeItems) {
							int serverId = (Integer) treeItem.getData(NODE_ID);
							if (serverId == sid) {
								serverItemSelected(treeItem, false);
								String[] containers = favorite.getFavorite()
										.split(":");
								TreeItem[] dbItems = treeItem.getItems();
								for (TreeItem dbItem : dbItems) {
									if (dbItem.getText().equals(containers[1])) {
										tree.setSelection(dbItem);
										dbContainerItemSelected(dbItem, false);
										TreeItem[] dataItems = dbItem
												.getItems();
										for (int i = 2; i < containers.length; i++) {
											for (TreeItem dataItem : dataItems) {
												if (dataItem.getText().equals(
														containers[i])) {
													tree.setSelection(dataItem);
													dbContainerItemSelected(dataItem, false);
													dataItems = dataItem
															.getItems();
													break;
												}
											}

										}
									}
								}

							}
						}

					}
				});
			}
		}
	}

	private void initServers() {
		java.util.List<Server> servers = service1.listAll();

		for (Server server : servers) {
			addServerTreeItem(server);
		}
		rootRedisServers.setExpanded(true);
		rootRedisServers.setData(ITEM_OPENED, true);
	}

	private void addDBTreeItem(int server, TreeItem serverItem) {
		int amount = service1.listDBs(server);
		for (int i = 0; i < amount; i++) {
			TreeItem dbItem = new TreeItem(serverItem, SWT.NONE);
			dbItem.setText(DB_PREFIX + i);
			dbItem.setData(NODE_ID, i);
			dbItem.setData(NODE_TYPE, NodeType.DATABASE);
			dbItem.setImage(dbImage);
		}
		if (amount > 0) {
			serverItem.setExpanded(true);
			serverItem.setData(ITEM_OPENED, true);
		}
	}

	private TreeItem addServerTreeItem(Server server) {
		TreeItem treeItem = new TreeItem(rootRedisServers, 0);
		treeItem.setText(server.getName());
		treeItem.setData(NODE_ID, server.getId());
		treeItem.setData(NODE_TYPE, NodeType.SERVER);
		treeItem.setImage(redisImage);
		
		return treeItem;
	}

	private void addServer() {
		AddServerDialog dialog = new AddServerDialog(shlRedisClient,
				SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		Server server = (Server) dialog.open();

		if (server != null) {
			server.setId(service1.add(server.getName(), server.getHost(),
					server.getPort()));
			TreeItem item = addServerTreeItem(server);
			serverItemSelected(item, false);
		}
	}

	private void updateServer() {
		TreeItem[] items = tree.getSelection();
		if (items.length != 0) {
			NodeType type = (NodeType) items[0].getData(NODE_TYPE);
			if (type == NodeType.SERVER) {
				int id = (Integer) items[0].getData(NODE_ID);

				Server server = service1.listById(id);
				UpdateServerDialog dialog = new UpdateServerDialog(
						shlRedisClient,
						SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, server);
				server = (Server) dialog.open();
				if (server != null) {
					service1.update(id, server.getName(), server.getHost(),
							server.getPort());
					items[0].setText(server.getName());
					items[0].removeAll();
					serverItemSelected(items[0], false);
				}

			} else
				MessageDialog.openError(shlRedisClient, "error",
						"please select a server to update!");
		}
	}

	private void removeServer() {
		TreeItem[] items = tree.getSelection();
		if (items.length != 0) {
			NodeType type = (NodeType) items[0].getData(NODE_TYPE);
			if (type == NodeType.SERVER) {
				boolean ok = MessageDialog.openConfirm(shlRedisClient,
						"remove server", "Are you sure remove this server?");
				if (ok) {
					int id = ((Integer) (items[0].getData(NODE_ID))).intValue();
					items[0].dispose();
					service1.delete(id);

				}
			} else
				MessageDialog.openError(shlRedisClient, "error",
						"please select a server to remove!");
		}
	}

	

	

	private void renameContainer() {
		TreeItem item;

		TreeItem[] items = tree.getSelection();
		if (items.length > 0
				&& items[0].getData(NODE_TYPE) == NodeType.CONTAINER) {
			item = items[0];
			ContainerInfo info = new ContainerInfo();
			parseContainer(item, info);

			RenameKeysDialog dialog = new RenameKeysDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
					info.getServerName(), info.getDb(), info.getContainer());
			RenameInfo rinfo = (RenameInfo) dialog.open();

			if (rinfo != null
					&& !(rinfo.getNewContainer().equals(info.getContainer()))) {
				Set<String> result = service2.renameContainer(info.getId(),
						info.getDb(), info.getContainer(),
						rinfo.getNewContainer(), rinfo.isOverwritten());
				item.getParentItem().setData(ITEM_OPENED, false);
				dbContainerItemSelected(item.getParentItem(), false);
				if (!rinfo.isOverwritten() && result.size() > 0) {
					String failString = "Rename following keys failed because of exist:\n";
					for (String container : result)
						failString += container + "\n";
					MessageDialog.openConfirm(shlRedisClient,
							"rename keys result", failString);
				}
			}
		} else
			MessageDialog.openError(shlRedisClient, "error",
					"please select a container to rename");

	}

	private void deleteCotainer(TreeItem item) {
		boolean ok = MessageDialog.openConfirm(shlRedisClient, "delete keys",
				"Are you sure delete all keys under it?");
		if (ok) {
			ContainerInfo info = new ContainerInfo();
			parseContainer(item, info);
			service2.deleteContainer(info.getId(), info.getDb(),
					info.getContainer());
			item.dispose();

		}
	}

	private void addFavorite() {
		TreeItem[] items = tree.getSelection();
		if (items.length > 0 && items[0].getData(NODE_TYPE) != NodeType.SERVER) {
			ContainerInfo cinfo = new ContainerInfo();
			parseContainer(items[0], cinfo);
			AddFavoriteDialog dialog = new AddFavoriteDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, text.getText());
			String name = (String) dialog.open();
			if (name != null)
				service3.add(cinfo.getId(), name, text.getText());
		} else
			MessageDialog.openError(shlRedisClient, "error",
					"please select favorite to add");

		removeFavoriteMenuItem();
		addFavoriteMenuItem();
	}
	
	private void treeItemSelected(boolean refresh) {
		TreeItem[] items = tree.getSelection();
		NodeType type = (NodeType) items[0].getData(NODE_TYPE);
		switch (type) {
		case ROOT:
			rootItemSelected(refresh);
			break;
		case SERVER:
			serverItemSelected(items[0], refresh);
			break;
		case DATABASE:
		case CONTAINER:
			dbContainerItemSelected(items[0], refresh);
			break;
		default:
			break;
		}
	}
	
	private void dbContainerItemSelected(TreeItem itemSelected, boolean refresh) {
		tree.setSelection(itemSelected);
		ContainerInfo info = new ContainerInfo();
		parseContainer(itemSelected, info);
		String container = (info.getContainer() == null) ? "" : info
				.getContainer();
		text.setText(info.getServerName() + ":" + DB_PREFIX + info.getDb()
				+ ":" + container);
		table.removeAll();
		mntmAdd_Favorite.setEnabled(true);

		Set<Node> cnodes = service2.listContainers(info.getId(), info.getDb(),
				info.getContainer());

		if(refresh)
			itemSelected.setData(ITEM_OPENED, false);
		
		if (itemSelected.getData(ITEM_OPENED) == null
				|| ((Boolean) (itemSelected.getData(ITEM_OPENED)) == false)) {
			itemSelected.removeAll();

			for (Node node : cnodes) {
				TreeItem item = new TreeItem(itemSelected, SWT.NONE);
				item.setText(node.getKey());
				item.setData(NODE_TYPE, node.getType());
				item.setImage(containerImage);
				
			}
			itemSelected.setExpanded(true);
			itemSelected.setData(ITEM_OPENED, true);
		}
		for (Node node : cnodes) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { node.getKey(),
					node.getType().toString() });
			item.setImage(containerImage);
		}

		Set<DataNode> knodes = service2.listContainerKeys(info.getId(),
				info.getDb(), info.getContainer());

		for (DataNode node1 : knodes) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { node1.getKey(),
					node1.getType().toString(), String.valueOf(node1.getSize()) });
			switch(node1.getType()) {
			case STRING:
				item.setImage(strImage);
				break;
			case SET:
				item.setImage(setImage);
				break;
			case LIST:
				item.setImage(listImage);
				break;
			case HASH:
				item.setImage(hashImage);
				break;
			case SORTEDSET:
				item.setImage(zsetImage);
				break;
			default:
				break;
			}
		}
	}
	
	private void rootItemSelected(boolean refresh) {
		tree.setSelection(rootRedisServers);
		text.setText("");
		table.removeAll();
		mntmAdd_Favorite.setEnabled(false);

		if(refresh)
			rootRedisServers.setData(ITEM_OPENED, false);
		
		if (rootRedisServers.getData(ITEM_OPENED) == null
				|| ((Boolean) (rootRedisServers.getData(ITEM_OPENED)) == false)) {
			rootRedisServers.removeAll();
			initServers();
			
		}

		java.util.List<Server> servers = service1.listAll();

		for (Server server : servers) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { server.getName(),
					NodeType.SERVER.toString() });
			item.setImage(redisImage);
		}

	}
	
	private void serverItemSelected(TreeItem selectedItem, boolean refresh) {
		tree.setSelection(selectedItem);
		text.setText(selectedItem.getText() + ":");
		table.removeAll();
		mntmAdd_Favorite.setEnabled(false);

		if(refresh)
			selectedItem.setData(ITEM_OPENED, false);
		
		if (selectedItem.getData(ITEM_OPENED) == null
				|| ((Boolean) (selectedItem.getData(ITEM_OPENED)) == false)) {
			selectedItem.removeAll();
			addDBTreeItem((Integer) selectedItem.getData(NODE_ID), selectedItem);
		}

		int dbs = service1.listDBs((Integer) selectedItem.getData(NODE_ID));
		for (int i = 0; i < dbs; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { DB_PREFIX + i,
					NodeType.DATABASE.toString() });
			item.setData(NODE_ID, i);
			item.setImage(dbImage);
		}

	}
	
	private void newString() {
		TreeItem item;

		TreeItem[] items = tree.getSelection();
		if (items.length > 0
				&& (items[0].getData(NODE_TYPE) == NodeType.DATABASE || items[0]
						.getData(NODE_TYPE) == NodeType.CONTAINER)) {
			item = items[0];
			ContainerInfo cinfo = new ContainerInfo();
			parseContainer(item, cinfo);
			NewStringDialog dialog = new NewStringDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
					cinfo.getServerName(), cinfo.getDb(), cinfo.getContainer());
			StringInfo info = (StringInfo) dialog.open();
			if (info != null) {
				service2.addString(cinfo.getId(), cinfo.getDb(), info.getKey(),
						info.getValue());
				item.setData(ITEM_OPENED, false);
				dbContainerItemSelected(item, false);
			}
		}

		else
			MessageDialog.openError(shlRedisClient, "error",
					"please select a holder to new string");

	}
	
	private void newList() {
		TreeItem item = null;

		TreeItem[] items = tree.getSelection();
		if (items.length > 0
				&& (items[0].getData(NODE_TYPE) == NodeType.DATABASE || items[0]
						.getData(NODE_TYPE) == NodeType.CONTAINER)) {
			item = items[0];

			ContainerInfo cinfo = new ContainerInfo();
			parseContainer(item, cinfo);
			NewListDialog dialog = new NewListDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
					cinfo.getServerName(), cinfo.getDb(), cinfo.getContainer());
			ListInfo info = (ListInfo) dialog.open();
			if (info != null) {
				service4.add(cinfo.getId(), cinfo.getDb(), info.getKey(),
						info.getValues(), info.isHeadTail(), info.isExist());
				item.setData(ITEM_OPENED, false);
				dbContainerItemSelected(item, false);
			}
		} else
			MessageDialog.openError(shlRedisClient, "error",
					"please select a holder to new list");
	}
	
	private void newSet() {
		TreeItem item = null;

		TreeItem[] items = tree.getSelection();
		if (items.length > 0
				&& (items[0].getData(NODE_TYPE) == NodeType.DATABASE || items[0]
						.getData(NODE_TYPE) == NodeType.CONTAINER)) {
			item = items[0];

			ContainerInfo cinfo = new ContainerInfo();
			parseContainer(item, cinfo);
			NewSetDialog dialog = new NewSetDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
					cinfo.getServerName(), cinfo.getDb(), cinfo.getContainer());
			SetInfo info = (SetInfo) dialog.open();
			if (info != null) {
				service5.add(cinfo.getId(), cinfo.getDb(), info.getKey(),
						info.getValues());
				item.setData(ITEM_OPENED, false);
				dbContainerItemSelected(item, false);
			}
		} else
			MessageDialog.openError(shlRedisClient, "error",
					"please select a holder to new set");
	}
	
	private void newZSet() {
		TreeItem item = null;

		TreeItem[] items = tree.getSelection();
		if (items.length > 0
				&& (items[0].getData(NODE_TYPE) == NodeType.DATABASE || items[0]
						.getData(NODE_TYPE) == NodeType.CONTAINER)) {
			item = items[0];

			ContainerInfo cinfo = new ContainerInfo();
			parseContainer(item, cinfo);
			NewZSetDialog dialog = new NewZSetDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
					cinfo.getServerName(), cinfo.getDb(), cinfo.getContainer());
			ZSetInfo info = (ZSetInfo) dialog.open();
			if (info != null) {
				service6.add(cinfo.getId(), cinfo.getDb(), info.getKey(),
						info.getValues());
				item.setData(ITEM_OPENED, false);
				dbContainerItemSelected(item, false);
			}
		} else
			MessageDialog.openError(shlRedisClient, "error",
					"please select a holder to new sorted set");
		
	}
	
	private void newHash() {
		TreeItem item = null;

		TreeItem[] items = tree.getSelection();
		if (items.length > 0
				&& (items[0].getData(NODE_TYPE) == NodeType.DATABASE || items[0]
						.getData(NODE_TYPE) == NodeType.CONTAINER)) {
			item = items[0];

			ContainerInfo cinfo = new ContainerInfo();
			parseContainer(item, cinfo);
			NewHashDialog dialog = new NewHashDialog(shlRedisClient,
					SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
					cinfo.getServerName(), cinfo.getDb(), cinfo.getContainer());
			HashInfo info = (HashInfo) dialog.open();
			if (info != null) {
				service7.add(cinfo.getId(), cinfo.getDb(), info.getKey(),
						info.getValues());
				item.setData(ITEM_OPENED, false);
				dbContainerItemSelected(item, false);
			}
		} else
			MessageDialog.openError(shlRedisClient, "error",
					"please select a holder to new hash");
		
	}

	
}